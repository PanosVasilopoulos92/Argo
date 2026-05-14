package org.viators.argo.requisition.line;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RequisitionLineRepository extends JpaRepository<RequisitionLineT, Long> {

    @EntityGraph(attributePaths = {"requisition", "catalogItem"})
    Optional<RequisitionLineT> findByPublicId(String publicId);

    @EntityGraph(attributePaths = {"requisition", "catalogItem"})
    List<RequisitionLineT> findByPublicIdIn(Collection<String> publicIds);

    @Query("""
        SELECT COUNT(l)
        FROM RequisitionLineT l
        WHERE l.requisition.id = :requisitionId
        """)
    long countByRequisitionIds(@Param("requisitionId") Long requisitionId);

    @Query("""
           select rl from RequisitionLineT rl
           join rl.requisition r
           left join fetch rl.poLines pol
           join pol.purchaseOrder po
           where r.id = :reqDatabaseId
           and po.purchaseOrderState != org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum.CANCELLED
           """)
    Set<RequisitionLineT> findAllByRequisition_Id(@Param("reqDatabaseId") Long reqDatabaseId);
}
