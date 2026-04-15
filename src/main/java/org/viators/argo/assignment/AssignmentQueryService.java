package org.viators.argo.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentQueryService {

    private final AssignmentRepository assignmentRepository;

    @Transactional(readOnly = true)
    public Set<AssignmentT> getCurrentCrewRosterForVessel(String vesselPublicId) {
        return assignmentRepository.findByVessel_PublicIdAndAssignmentStateAndActualSignedOffDateIsNull(
            vesselPublicId, AssignmentStateEnum.ACTIVE);
    }

    @Transactional(readOnly = true)
    public boolean hasSeafarerActiveAssignment(String seafarerPublicId) {
        return assignmentRepository.existsBySeafarer_PublicIdAndAssignmentStateAndActualSignedOffDateIsNull(
            seafarerPublicId, AssignmentStateEnum.ACTIVE);
    }
}
