package org.viators.argo.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.assignment.dto.projection.ActiveAssignmentInfo;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AssignmentQueryService {

    private final AssignmentRepository assignmentRepository;

    public Set<AssignmentT> getCurrentCrewRosterForVessel(String vesselPublicId) {
        return assignmentRepository.findByVessel_PublicIdAndAssignmentStateAndActualSignedOffDateIsNull(
            vesselPublicId, AssignmentStateEnum.ACTIVE);
    }

    public boolean hasSeafarerActiveAssignment(String seafarerPublicId) {
        return assignmentRepository.existsBySeafarer_PublicIdAndAssignmentStateAndActualSignedOffDateIsNull(
            seafarerPublicId, AssignmentStateEnum.ACTIVE);
    }

    public Optional<ActiveAssignmentInfo> getActiveAssignmentInfoForSeafarer(String seafarerPublicId) {
        return assignmentRepository.findActiveAssignmentInfoForSeafarer(seafarerPublicId);
    }
}
