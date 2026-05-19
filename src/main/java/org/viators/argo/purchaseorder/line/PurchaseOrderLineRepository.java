package org.viators.argo.purchaseorder.line;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLineT, Long> {

    @EntityGraph(attributePaths = {"goodsReceiptLines"})
    Optional<PurchaseOrderLineT> findByPublicId(String publicId);

    @EntityGraph(attributePaths = {"goodsReceiptLines", "requisitionLine"})
    List<PurchaseOrderLineT> findAllByPublicIdIn(Collection<String> publicIds);
}
