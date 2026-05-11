package org.viators.argo.purchaseorder.line;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLineT, Long> {

    @EntityGraph(attributePaths = {"goodsReceiptLines", "requisitionLine"})
    List<PurchaseOrderLineT> findAllByPublicIdIsIn(Collection<String> publicIds);

}
