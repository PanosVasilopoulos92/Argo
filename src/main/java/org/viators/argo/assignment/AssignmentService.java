package org.viators.argo.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.assignment.dto.request.CreateAssignmentRequest;
import org.viators.argo.assignment.dto.request.SignOffSeafarerRequest;
import org.viators.argo.assignment.dto.response.AssignmentDetailsResponse;
import org.viators.argo.assignment.dto.response.AssignmentsHistOfVesselResponse;
import org.viators.argo.assignment.dto.response.AssignmentsHistOfSeafarerResponse;
import org.viators.argo.assignment.dto.response.CrewRosterResponse;
import org.viators.argo.certificate.person.PersonCertificateT;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.person.seafarer.SeafarerService;
import org.viators.argo.person.seafarer.SeafarerT;
import org.viators.argo.vessel.VesselService;
import org.viators.argo.vessel.VesselT;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SeafarerService seafarerService;
    private final VesselService vesselService;

    @Transactional
    public AssignmentDetailsResponse create(CreateAssignmentRequest request) {
        SeafarerT seafarer = seafarerService.getResourceByPublicId(request.seafarerPublicId());
        VesselT vessel = vesselService.getResourceByPublicId(request.vesselPublicId());

        validateAssignment(
            seafarer.getPublicId(), seafarer.getStatus(), vessel.getPublicId(), vessel.getStatus()
        );

        AssignmentT assignment = request.toEntity();
        assignment.setSeafarer(seafarer);
        assignment.setVessel(vessel);

        assignment = assignmentRepository.save(assignment);
        return AssignmentDetailsResponse.from(assignment, seafarer.getCertificates());
    }

    @Transactional
    public AssignmentDetailsResponse signOffSeafarer(String assignmentPublicId, SignOffSeafarerRequest request) {
        AssignmentT assignment =
            assignmentRepository.findByPublicId(assignmentPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "publicId", assignmentPublicId));

        if (!AssignmentStateEnum.ACTIVE.equals(assignment.getAssignmentState())) {
            throw new BusinessValidationException("Assignment with public Id: %s is not active".formatted(assignmentPublicId));
        }

        if (assignment.getSignOnDate().isAfter(request.actualSignedOffDate())) {
            throw new BusinessValidationException("Sign on date cannot be after sign off date");
        }

        request.signOffSeafarer(assignment);
        assignment.setAssignmentState(AssignmentStateEnum.COMPLETED);

        return AssignmentDetailsResponse.from(assignment, Set.of());
    }

    @Transactional
    public void cancelAssignment(String assignmentPublicId) {
        AssignmentT assignment = assignmentRepository.findByPublicId(assignmentPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Assignment", "publicId", assignmentPublicId));

        if (!AssignmentStateEnum.ACTIVE.equals(assignment.getAssignmentState())) {
            throw new InvalidStateException("Assignment with public Id: %s is not active. Reconsider your action.");
        }

        assignment.setAssignmentState(AssignmentStateEnum.CANCELLED);
    }

    @Transactional(readOnly = true)
    public Page<CrewRosterResponse> getCurrentCrewRosterForVessel(String vesselPublicId, Pageable pageable) {
        return assignmentRepository.findByVessel_PublicIdAndAssignmentStateAndActualSignedOffDateIsNull(
                vesselPublicId, AssignmentStateEnum.ACTIVE, pageable)
            .map(CrewRosterResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<AssignmentsHistOfSeafarerResponse> getAssignmentsHistForSeafarer(String seafarerPublicId, Pageable pageable) {
        return assignmentRepository.findAssignmentHistForSeaman(seafarerPublicId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AssignmentsHistOfVesselResponse> getAssignmentsHistForVessel(String vesselPublicId, Pageable pageable) {
        return assignmentRepository.findAssignmentsHistForVessel(vesselPublicId, pageable);
    }

    // Helper private methods
    private void validateAssignment(String seafarerPublicId,
                                    ResourceStatusEnum seafarerStatus,
                                    String vesselPublicId,
                                    ResourceStatusEnum vesselStatus) {

        if (ResourceStatusEnum.INACTIVE.equals(seafarerStatus)) {
            throw new BusinessValidationException(
                "Seafarer with public Id: %s is inactive and cannot get assigned to any vessel"
                    .formatted(seafarerPublicId));
        }

        if (ResourceStatusEnum.INACTIVE.equals(vesselStatus)) {
            throw new BusinessValidationException(
                "Vessel with public Id: %s is inactive and no seafarer can be assigned to it"
                    .formatted(vesselPublicId));
        }

        assignmentRepository.findBySeafarer_PublicIdAndActualSignedOffDateIsNull(seafarerPublicId)
            .ifPresent(existing -> {
                throw new InvalidStateException(
                    "Seaman already deployed to another vessel." +
                        "Tried to assign seaman with public Id: %s to vessel with public Id: %s"
                            .formatted(seafarerPublicId, vesselPublicId)
                );
            });
    }
}
