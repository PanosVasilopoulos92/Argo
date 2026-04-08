package org.viators.argo.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentT, Long> {
    Optional<AssignmentT> findBySeafarer_PublicIdAndActualSignedOffDateIsNotNull(String seafarerPublicId);
}
