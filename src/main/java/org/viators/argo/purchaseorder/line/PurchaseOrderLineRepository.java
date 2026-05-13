package org.viators.argo.purchaseorder.line;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLineT, Long> {

    @EntityGraph(attributePaths = {"goodsReceiptLines", "requisitionLine"})
    List<PurchaseOrderLineT> findAllByPublicIDsIn(Collection<String> publicIds);

    @Query("""
           select pol from PurchaseOrderLineT pol
           join pol.purchaseOrder po
           where po.publicId in :poIds
           and po.purchaseOrderState != org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum.CANCELLED
           """)
    List<PurchaseOrderLineT> findAllPOLinesForPOs(@Param("poIds") List<Long> poIds);
}
