package org.viators.argo.purchaseorder;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderT, Long>, JpaSpecificationExecutor<PurchaseOrderT> {

    @EntityGraph(attributePaths = {"supplier", "requisition", "poLines"})
    Optional<PurchaseOrderT> findByPublicId(String publicId);

    @Override
    @EntityGraph(attributePaths = {"supplier", "requisition"})
    Page<PurchaseOrderT> findAll(@NonNull Specification<PurchaseOrderT> spec, @NonNull Pageable pageable);

    List<PurchaseOrderT> findAllByRequisition_PublicId(String requisitionPublicId);

    @Query("""
        select po from PurchaseOrderT po
        join fetch po.poLines pol
        where po.id = :databaseId
        """)
    Optional<PurchaseOrderT> findByDatabaseIdWithPoLines(@Param("databaseId") Long poDatabaseId);

    @Query("""
           select count(gr) > 0 from GoodsReceiptT gr
           where gr.purchaseOrder.publicId = :poPublicId
           and gr.receiptState != org.viators.argo.goodsreceipt.enums.GoodsReceiptStateEnum.CANCELLED
           """)
    boolean hasPONotCancelledGoodsReceipts(@Param("poPublicId") String poPublicId);

    @Query("""
        select po from PurchaseOrderT po
        join fetch po.supplier s
        join fetch po.poLines pol
        where po.publicId = :poPublicId
        """)
    Optional<PurchaseOrderT> findPurchaseOrderWithLinesAndSupplier(@Param("poPublicId") String poPublicId);
}
