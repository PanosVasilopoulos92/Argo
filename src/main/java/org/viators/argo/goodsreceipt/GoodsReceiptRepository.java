package org.viators.argo.goodsreceipt;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoodsReceiptRepository extends JpaRepository<GoodsReceiptT, Long>, JpaSpecificationExecutor<GoodsReceiptT> {

    @EntityGraph(attributePaths = {GoodsReceiptT_.PURCHASE_ORDER, GoodsReceiptT_.GOODS_RECEIPT_LINES})
    Optional<GoodsReceiptT> findByPublicId(String publicId);

    @Override
    @EntityGraph(attributePaths = {GoodsReceiptT_.PURCHASE_ORDER})
    @NonNull
    Page<GoodsReceiptT> findAll(@NonNull Specification<GoodsReceiptT> spec, @NonNull Pageable pageable);

    @EntityGraph(attributePaths = {GoodsReceiptT_.PURCHASE_ORDER})
    Page<GoodsReceiptT> findByPurchaseOrder_PublicId(String purchaseOrderPublicId, Pageable pageable);
}
