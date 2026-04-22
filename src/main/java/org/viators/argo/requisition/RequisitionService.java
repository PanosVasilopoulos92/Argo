package org.viators.argo.requisition;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.item.ItemQueryService;
import org.viators.argo.item.ItemT;
import org.viators.argo.person.PersonT;
import org.viators.argo.requisition.dto.request.CreateRequisitionLineRequest;
import org.viators.argo.requisition.dto.request.CreateRequisitionRequest;
import org.viators.argo.requisition.dto.response.RequisitionDetailsResponse;
import org.viators.argo.requisition.enums.RequisitionTypeEnum;
import org.viators.argo.requisition.sequence.RequisitionSequenceRepository;
import org.viators.argo.requisition.sequence.RequisitionSequenceT;
import org.viators.argo.user.UserService;
import org.viators.argo.user.UserT;
import org.viators.argo.vessel.VesselQueryService;
import org.viators.argo.vessel.VesselT;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final VesselQueryService vesselQueryService;
    private final ItemQueryService itemQueryService;
    private final RequisitionSequenceRepository requisitionSequenceRepository;
    private final UserService userService;

    @Transactional
    public RequisitionDetailsResponse create(String keycloakId, CreateRequisitionRequest request) {
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

        UserT loggedInUser = userService.getUser(keycloakId);
        PersonT raisedBy = loggedInUser.getPerson();
        if (raisedBy.getStatus().equals(ResourceStatusEnum.INACTIVE)) {
            throw new InvalidStateException("Person with public Id: %s is inactive. Requisition cannot continue further"
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
                    .formatted(raisedBy.getPublicId()));
            }
            RequisitionLineT requisitionLine = lineRequest.toEntity(item);
            requisition.addReqLine(requisitionLine);
        }

        return RequisitionDetailsResponse.from(requisition);
    }

    private String generateReqNumber() {
        int year = LocalDate.now().getYear();
        RequisitionSequenceT lastSequenceForCurrentYear = requisitionSequenceRepository.findTop1ByYearOrderByLastValueDesc(year)
            .orElse(new RequisitionSequenceT(year, 0L));

        RequisitionSequenceT nextSequenceForCurrentYear = new RequisitionSequenceT(
            year, lastSequenceForCurrentYear.getLastValue() + 1L
        );
        requisitionSequenceRepository.save(nextSequenceForCurrentYear);

        return ("REQ-" + nextSequenceForCurrentYear.getYear())
            .concat("-" + String.format("06%d", nextSequenceForCurrentYear.getLastValue()));
    }

    // Private helper methods
    private void checkRequisitionTypeValidity(CreateRequisitionRequest request) {
        if (request.requisitionType().equals(RequisitionTypeEnum.OFFICE)
            && StringUtils.hasText(request.targetVesselPublicId())) {
            throw new InvalidStateException("If requisition is of type 'OFFICE' you cannot provide target vessel.");
        }

        if (request.requisitionType().equals(RequisitionTypeEnum.VESSEL)
            && !StringUtils.hasText(request.targetVesselPublicId())) {
            throw new InvalidStateException("If requisition is of type 'VESSEL' you must also provide the target vessel.");
        }
    }
}
