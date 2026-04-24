package org.viators.argo.requisition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionLineRepository extends JpaRepository<RequisitionLineT, Long> {

    @Query("""
        SELECT COUNT(l)
        FROM RequisitionLineT l
        WHERE l.requisition.id = :requisitionId
        """)
    long countByRequisitionIds(@Param("requisitionId") Long requisitionId);
}
