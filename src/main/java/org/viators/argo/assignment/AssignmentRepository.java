package org.viators.argo.assignment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.viators.argo.common.enums.ResourceStatusEnum;

import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentT, Long> {

    @EntityGraph(attributePaths = {"seafarer", "vessel"})
    Optional<AssignmentT> findByPublicId(String publicId);

    Page<AssignmentT> findByVessel_PublicIdAndStatusAndActualSignedOffDateIsNull(
        String vesselPublicId,ResourceStatusEnum status, Pageable pageable);

    Optional<AssignmentT> findBySeafarer_PublicIdAndActualSignedOffDateIsNotNull(String seafarerPublicId);
}
