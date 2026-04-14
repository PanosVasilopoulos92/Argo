package org.viators.argo.assignment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentT, Long> {

    @EntityGraph(attributePaths = {"seafarer", "vessel"})
    Optional<AssignmentT> findByPublicId(String publicId);

    Page<AssignmentT> findByVessel_PublicIdAndAssignmentStateAndActualSignedOffDateIsNull(
        String vesselPublicId, AssignmentStateEnum status, Pageable pageable);

    Optional<AssignmentT> findBySeafarer_PublicIdAndActualSignedOffDateIsNotNull(String seafarerPublicId);
}
