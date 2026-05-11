package org.viators.argo.requisition;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.item.ItemQueryService;
import org.viators.argo.item.ItemT;
import org.viators.argo.person.PersonQueryService;
import org.viators.argo.person.PersonT;
import org.viators.argo.requisition.dto.request.*;
import org.viators.argo.requisition.dto.response.ReqDetailsWithRelationshipsSummaryResponse;
import org.viators.argo.requisition.dto.response.RequisitionDetailsResponse;
import org.viators.argo.requisition.dto.response.RequisitionSummaryResponse;
import org.viators.argo.requisition.enums.RequisitionStateEnum;
import org.viators.argo.requisition.enums.RequisitionTypeEnum;
import org.viators.argo.requisition.line.RequisitionLineT;
import org.viators.argo.requisition.sequence.RequisitionSequenceRepository;
import org.viators.argo.requisition.sequence.RequisitionSequenceT;
import org.viators.argo.user.UserLevelEnum;
import org.viators.argo.user.UserService;
import org.viators.argo.user.UserT;
import org.viators.argo.vessel.VesselQueryService;
import org.viators.argo.vessel.VesselT;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final VesselQueryService vesselQueryService;
    private final ItemQueryService itemQueryService;
    private final RequisitionSequenceRepository requisitionSequenceRepository;
    private final RequisitionApprovalHistRepository reqApprovalHistRepository;
    private final UserService userService;
    private final PersonQueryService personQueryService;


    @Transactional
    public RequisitionDetailsResponse createDraft(String keycloakId, CreateRequisitionRequest request) {
        RequisitionT requisition = request.toEntity();

        checkRequisitionTypeValidity(request);

        if (StringUtils.hasText(request.targetVesselPublicId())) {
            VesselT targetVessel = vesselQueryService.getResourceByPublicId(request.targetVesselPublicId());
            if (targetVessel.getStatus().equals(ResourceStatusEnum.INACTIVE)) {
                throw new InvalidStateException("Vessel with public Id: %s is inactive. Requisition cannot continue further"
                    .formatted(targetVessel.getPublicId()));
            }

            requisition.setTargetVessel(targetVessel);
        }

        PersonT raisedBy = personQueryService.getPersonByPublicId(request.raisedByPublicId());
        if (ResourceStatusEnum.INACTIVE.equals(raisedBy.getStatus())) {
            throw new InvalidStateException("Person with public Id: %s is inactive. Requisition cannot proceed"
                .formatted(raisedBy.getPublicId()));
        }

        requisition.setRaisedBy(raisedBy);
        requisition.setRequisitionNumber(generateReqNumber());

        requisitionRepository.save(requisition);

        // Handle save of lines
        for (CreateRequisitionLineRequest lineRequest : request.lineRequests()) {
            ItemT item = itemQueryService.getResourcePublicId(lineRequest.itemPublicId());
            if (item.getStatus().equals(ResourceStatusEnum.INACTIVE)) {
                throw new InvalidStateException("Item with public Id: %s is inactive. Requisition cannot continue further"
                    .formatted(item.getPublicId()));
            }
            RequisitionLineT requisitionLine = lineRequest.toEntity(item);
            requisition.addReqLine(requisitionLine);
        }

        return RequisitionDetailsResponse.from(requisition);
    }

    @Transactional
    public RequisitionDetailsResponse submitRequisition(String keycloakId,
                                                        String reqPublicId,
                                                        SubmitRequisitionRequest request
    ) {
        RequisitionT requisition = loadResourceAndCheckStatusAndVersion(reqPublicId, request.version());
        if (isReqStateTransitionNotValid(requisition.getRequisitionState(), RequisitionStateEnum.SUBMITTED)) {
            throw new InvalidStateException("Requisition with public Id: %s is in '%s' state and cannot transition to state 'SUBMITTED'"
                .formatted(requisition.getPublicId(), requisition.getRequisitionState().name()));
        }

        // Relationships validation
        validateReqLine(requisition.getLines());
        Long vesselId = requisition.getTargetVessel() != null
            ? requisition.getTargetVessel().getId()
            : null;
        relationshipsStillActive(requisition.getRequisitionType(), requisition.getRaisedBy().getId(), vesselId);

        UserT user = userService.getUser(keycloakId);
        requisition.setSubmittedAt(Instant.now());
        requisition.setSubmittedBy(user.getUsername());

        return RequisitionDetailsResponse.from(requisitionRepository.save(requisition));
    }

    @Transactional
    public RequisitionDetailsResponse approveRequisition(String keycloakId,
                                                         String reqPublicId,
                                                         ApproveRequisitionRequest request
    ) {
        RequisitionT requisition = loadResourceAndCheckStatusAndVersion(reqPublicId, request.version());
        UserT loggedInUser = userService.getUser(keycloakId);

        if (loggedInUser.getLevel().equals(UserLevelEnum.LEVEL_1)) {
            throw new BusinessValidationException("Users with level 1 are not able to approve any requisition");
        }

        RequisitionStateEnum targetState = loggedInUser.getLevel().equals(UserLevelEnum.LEVEL_5)
            ? RequisitionStateEnum.FINALIZED
            : RequisitionStateEnum.APPROVED;

        if (isReqStateTransitionNotValid(requisition.getRequisitionState(), targetState)) {
            throw new InvalidStateException("Requisition with public Id: %s is in '%s' state and cannot transition to state '%s'"
                .formatted(requisition.getPublicId(), requisition.getRequisitionState().name(), targetState.name()));
        }

        validateApproverIsNotCreatorOrSubmitter(requisition, loggedInUser.getUsername());

        RequisitionApprovalHistoryT requisitionApprovalHistory = validateAndBuildApprovalHistoryEntry(
            reqPublicId, request, loggedInUser, targetState, requisition
        );

        requisition.setApprovedAt(Instant.now());
        requisition.setApprovedBy(loggedInUser.getUsername());
        requisition.setApprovalRemarks(request.approvalRemarks());
        requisition.setRequisitionState(targetState);
        requisition.addReqApprovalHistoryEntry(requisitionApprovalHistory);

        return RequisitionDetailsResponse.from(requisitionRepository.save(requisition));
    }

    @Transactional
    public RequisitionDetailsResponse rejectRequisition(String keycloakId,
                                                        String reqPublicId,
                                                        RejectRequisitionRequest request
    ) {
        RequisitionT requisition = loadResourceAndCheckStatusAndVersion(reqPublicId, request.version());
        UserT loggedInUser = userService.getUser(keycloakId);

        if (isReqStateTransitionNotValid(requisition.getRequisitionState(), RequisitionStateEnum.REJECTED)) {
            throw new InvalidStateException("Requisition with public Id: %s is in '%s' state and cannot transition to state 'REJECTED'"
                .formatted(requisition.getPublicId(), requisition.getRequisitionState().name()));
        }

        validateApproverIsNotCreatorOrSubmitter(requisition, loggedInUser.getUsername());

        RequisitionApprovalHistoryT requisitionApprovalHistory = reqApprovalHistRepository
            .findTop1ByRequisition_PublicIdOrderByCreatedAtDesc(reqPublicId);

        if (requisitionApprovalHistory != null &&
            requisitionApprovalHistory.getApproverLevelAtAction().getOrdinal() >= loggedInUser.getLevel().getOrdinal()) {
            throw new BusinessValidationException("You cannot reject a requisition that has been approved from a user " +
                "with higher level than yours");
        }

        RequisitionApprovalHistoryT requisitionApprovalHistoryToInsert = new RequisitionApprovalHistoryT(
            requisition, loggedInUser.getUsername(), RequisitionStateEnum.REJECTED, loggedInUser.getLevel(), request.rejectedReason()
        );

        requisition.setRejectedAt(Instant.now());
        requisition.setRejectedBy(loggedInUser.getUsername());
        requisition.setRejectedReason(request.rejectedReason());
        requisition.setRequisitionState(RequisitionStateEnum.REJECTED);
        requisition.addReqApprovalHistoryEntry(requisitionApprovalHistoryToInsert);

        return RequisitionDetailsResponse.from(requisitionRepository.save(requisition));
    }

    @Transactional
    public void cancelRequisitionDraft(String keycloakId,
                                       String reqPublicId,
                                       CancelDraftRequisitionRequest request) {
        RequisitionT requisition = loadResourceAndCheckStatusAndVersion(reqPublicId, request.version());
        UserT loggedInUser = userService.getUser(keycloakId);

        if (isReqStateTransitionNotValid(requisition.getRequisitionState(), RequisitionStateEnum.CANCELLED)) {
            throw new InvalidStateException("Requisition with public Id: %s is in '%s' state and cannot transition to state 'CANCELLED'"
                .formatted(requisition.getPublicId(), requisition.getRequisitionState().name()));
        }

        requisition.setRequisitionState(RequisitionStateEnum.CANCELLED);
        requisition.setCancelledAt(Instant.now());
        requisition.setCancelledBy(loggedInUser.getUsername());

        requisition.getLines()
            .forEach(line -> line.setStatus(ResourceStatusEnum.INACTIVE));

        requisitionRepository.save(requisition);
    }

    // Read only methods
    @Transactional(readOnly = true)
    public ReqDetailsWithRelationshipsSummaryResponse getRequisitionDetailsWithRelationshipsSummary(String reqPublicId) {
        RequisitionT requisition = requisitionRepository.findByPublicId(reqPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Requisition", "publicId", reqPublicId));

        return ReqDetailsWithRelationshipsSummaryResponse.from(requisition);
    }

    @Transactional(readOnly = true)
    public Page<RequisitionSummaryResponse> getRequisitionFiltered(
        RequisitionSearchFilterRequest request, Pageable pageable
    ) {
        Specification<RequisitionT> specs = (root, query, cb) -> cb.conjunction();

        if (request.requisitionType() != null) {
            specs = specs.and(RequisitionSpecs.hasRequisitionType(request.requisitionType()));
        }
        if (StringUtils.hasText(request.vesselPublicId())) {
            specs = specs.and(RequisitionSpecs.hasVesselPublicId(request.vesselPublicId()));
        }
        if (request.states() != null && !request.states().isEmpty()) {
            specs = specs.and(RequisitionSpecs.hasState(request.states()));
        }
        if (StringUtils.hasText(request.raisedByPublicId())) {
            specs = specs.and(RequisitionSpecs.hasBeenRaisedBy(request.raisedByPublicId()));
        }
        if (request.priority() != null) {
            specs = specs.and(RequisitionSpecs.hasPriority(request.priority()));
        }
        if (StringUtils.hasText(request.reqNumber())) {
            specs = specs.and(RequisitionSpecs.hasRequisitionNumber(request.reqNumber()));
        }

        specs = specs.and(RequisitionSpecs.hasCreatedDate(request.createdDateFrom(), request.createdDateTo()));
        specs = specs.and(RequisitionSpecs.hasRequiredDate(request.requiredByDateFrom(), request.requiredByDateTo()));

        return requisitionRepository.findAll(specs, pageable)
            .map(RequisitionSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public RequisitionT getActiveRequisitionByDatabaseId(Long databaseId) {
        RequisitionT requisition = requisitionRepository.findById(databaseId)
            .orElseThrow(() -> new ResourceNotFoundException("Requisition", "Id", databaseId));

        if (requisition.getStatus() == ResourceStatusEnum.INACTIVE) {
            throw new InvalidStateException("Requisition with Id: %d is inactive".formatted(databaseId));
        }

        return requisition;
    }

    // Private helper methods
    private String generateReqNumber() {
        int year = LocalDate.now().getYear();
        RequisitionSequenceT lastSequenceForCurrentYear = requisitionSequenceRepository.findTop1ByYearOrderByLastValueDesc(year)
            .orElse(new RequisitionSequenceT(year, 0L, null));

        RequisitionSequenceT nextSequenceForCurrentYear = new RequisitionSequenceT(
            year,
            lastSequenceForCurrentYear.getLastValue() + 1L,
            null
        );

        String finalReqNumberFormat = ("REQ-" + nextSequenceForCurrentYear.getYear())
            .concat("-" + String.format("%06d", nextSequenceForCurrentYear.getLastValue()));

        nextSequenceForCurrentYear.setFinalFormattedValue(finalReqNumberFormat);

        requisitionSequenceRepository.save(nextSequenceForCurrentYear);

        return finalReqNumberFormat;
    }

    private void checkRequisitionTypeValidity(CreateRequisitionRequest request) {
        if (request.requisitionType().equals(RequisitionTypeEnum.OFFICE)
            && StringUtils.hasText(request.targetVesselPublicId())) {
            throw new InvalidStateException("If requisition is of type 'OFFICE' you cannot provide target vessel");
        }

        if (request.requisitionType().equals(RequisitionTypeEnum.VESSEL)
            && !StringUtils.hasText(request.targetVesselPublicId())) {
            throw new InvalidStateException("If requisition is of type 'VESSEL' you must also provide the target vessel");
        }
    }

    private boolean isReqStateTransitionNotValid(RequisitionStateEnum fromState, RequisitionStateEnum toState) {
        return !switch (fromState) {
            case DRAFT ->
                toState.equals(RequisitionStateEnum.SUBMITTED) || toState.equals(RequisitionStateEnum.CANCELLED);
            case SUBMITTED ->
                toState.equals(RequisitionStateEnum.REJECTED) || toState.equals(RequisitionStateEnum.APPROVED);
            case APPROVED ->
                toState.equals(RequisitionStateEnum.REJECTED) || toState.equals(RequisitionStateEnum.APPROVED) ||
                    toState.equals(RequisitionStateEnum.FINALIZED);
            case FINALIZED ->
                toState == RequisitionStateEnum.FULFILLED;
            case REJECTED, CANCELLED, FULFILLED -> false;
        };
    }

    private void validateApproverIsNotCreatorOrSubmitter(RequisitionT requisition, String approverUsername) {
        if (requisition.getCreatedBy().equals(approverUsername) ||
            requisition.getSubmittedBy().equals(approverUsername)) {
            throw new BusinessValidationException("Approver must be a different person than the one created or submitted the requisition." +
                "Created by %s and submitted by %s. Tried to get approved/rejected by %s"
                    .formatted(requisition.getCreatedBy(), requisition.getSubmittedBy(), approverUsername));
        }
    }

    private void validateReqLine(Set<RequisitionLineT> lines) {
        lines.forEach(line -> {
            ItemT item = itemQueryService.getItemByItemCode(line.getSnapShotItemCode());
            if (ResourceStatusEnum.INACTIVE.equals(item.getStatus())) {
                throw new InvalidStateException("Item with code: %s has been deactivated. Requisition cannot proceed"
                    .formatted(line.getSnapShotItemCode())
                );
            }
        });
    }

    private RequisitionT loadResourceAndCheckStatusAndVersion(String reqPublicId, Long requestResourceVersion) {
        RequisitionT requisition = requisitionRepository.findByPublicId(reqPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Requisition", "public Id", reqPublicId));

        if (ResourceStatusEnum.INACTIVE.equals(requisition.getStatus())) {
            throw new InvalidStateException("Requisition with public Id: %s is inactive. No further actions can be made."
                .formatted(requisition.getPublicId()));
        }

        if (!Objects.equals(requestResourceVersion, requisition.getVersion())) {
            throw new OptimisticLockException("Another user in the mean time has modified this resource. Please try again");
        }

        return requisition;
    }

    private void relationshipsStillActive(RequisitionTypeEnum reqType, Long personId, Long vesselId) {
        PersonT person = personQueryService.getPersonByDatabaseId(personId);
        if (ResourceStatusEnum.INACTIVE.equals(person.getStatus())) {
            throw new InvalidStateException("Person that created/raised requisition has been deactivated" +
                " Requisition cannot proceed");
        }

        if (RequisitionTypeEnum.VESSEL.equals(reqType)) {
            VesselT vessel = vesselQueryService.getResourceByDatabaseId(vesselId);
            if (ResourceStatusEnum.INACTIVE.equals(vessel.getStatus())) {
                throw new InvalidStateException("Vessel for which requisition has been created/raised is now deactivated" +
                    " Requisition cannot proceed");
            }
        }
    }

    private RequisitionApprovalHistoryT validateAndBuildApprovalHistoryEntry(String reqPublicId,
                                                                             ApproveRequisitionRequest request,
                                                                             UserT loggedInUser,
                                                                             RequisitionStateEnum action,
                                                                             RequisitionT requisition) {

        RequisitionApprovalHistoryT requisitionApprovalHistory = reqApprovalHistRepository
            .findTop1ByRequisition_PublicIdOrderByCreatedAtDesc(reqPublicId);

        if (requisitionApprovalHistory != null) {

            int previousApproverLevel = requisitionApprovalHistory.getApproverLevelAtAction().getOrdinal();
            int currentUserApproverLevel = loggedInUser.getLevel().getOrdinal();

            if (previousApproverLevel >= currentUserApproverLevel) {
                throw new BusinessValidationException("This requisition was approved by a user of level %d. It requires an approver of level %d. Your level is %d"
                    .formatted(previousApproverLevel, previousApproverLevel + 1, currentUserApproverLevel));
            }
        }

        requisitionApprovalHistory = new RequisitionApprovalHistoryT(
            requisition, loggedInUser.getUsername(), action, loggedInUser.getLevel(), request.approvalRemarks()
        );

        return requisitionApprovalHistory;
    }
}
