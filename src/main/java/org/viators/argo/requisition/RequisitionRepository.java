package org.viators.argo.requisition;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequisitionRepository extends JpaRepository<RequisitionT, Long> {

    @EntityGraph(attributePaths = {"lines"})
    Optional<RequisitionT> findByPublicId(String publicId);
}
