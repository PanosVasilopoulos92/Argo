package org.viators.argo.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.assignment.dto.request.CreateAssignmentRequest;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.person.seafarer.SeafarerService;
import org.viators.argo.person.seafarer.SeafarerT;
import org.viators.argo.vessel.VesselService;
import org.viators.argo.vessel.VesselT;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SeafarerService seafarerService;
    private final VesselService vesselService;

    @Transactional
    public String create(CreateAssignmentRequest request) {
        SeafarerT seafarer = seafarerService.getResourceByPublicId(request.seafarerPublicId());
        VesselT vessel = vesselService.getResourceByPublicId(request.vesselPublicId());

        validateAssignment(
            seafarer.getPublicId(), seafarer.getStatus(), vessel.getPublicId(), vessel.getStatus()
        );

        AssignmentT assignment = request.toEntity();
        assignment.setSeafarer(seafarer);
        assignment.setVessel(vessel);

        assignment = assignmentRepository.save(assignment);
        return assignment.getPublicId();
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

        assignmentRepository.findBySeafarer_PublicIdAndActualSignedOffDateIsNotNull(seafarerPublicId)
            .ifPresent(existing -> {
                throw new InvalidStateException(
                    "Seaman already deployed to another vessel." +
                        "Tried to assign seaman with public Id: %s to vessel with public Id: %s"
                            .formatted(seafarerPublicId, vesselPublicId)
                );
            });
    }
}
