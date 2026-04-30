package org.viators.argo.requisition;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequisitionLineRepository extends JpaRepository<RequisitionLineT, Long> {

    @EntityGraph(attributePaths = {"requisition"})
    Optional<RequisitionLineT> findByPublicId(String publicId);

    @Query("""
        SELECT COUNT(l)
        FROM RequisitionLineT l
        WHERE l.requisition.id = :requisitionId
        """)
    long countByRequisitionIds(@Param("requisitionId") Long requisitionId);
}
