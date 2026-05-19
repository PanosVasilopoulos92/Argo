package org.viators.argo.invoice;

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
import org.viators.argo.invoice.dto.response.InvoiceSummaryResponse;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceT, Long>, JpaSpecificationExecutor<InvoiceT> {

    boolean existsBySupplierInvoiceReferenceAndSupplier_Id(String paymentReference, Long supplierId);

    @Override
    @EntityGraph(attributePaths = {InvoiceT_.SUPPLIER, InvoiceT_.PURCHASE_ORDER})
    @NonNull
    Page<InvoiceT> findAll(@NonNull Specification<InvoiceT> spec, @NonNull Pageable pageable);

    Optional<InvoiceT> findByPublicId(String publicId);

    @Query("""
           select i from InvoiceT i
           join fetch i.invoiceLines il
           where i.publicId = :invPublicId
           """)
    Optional<InvoiceT> findByPublicIdWithLines(@Param("invPublicId") String invPublicId);

    @Query("""
           select distinct i from InvoiceT i
           left join fetch i.invoiceLines il
           left join fetch il.poLine
           left join fetch i.supplier
           left join fetch i.purchaseOrder
           where i.publicId = :publicId
           """)
    Optional<InvoiceT> findInvoiceByPublicIdWithDetails(@Param("publicId") String publicId);

    @EntityGraph(attributePaths = {InvoiceT_.SUPPLIER, InvoiceT_.PURCHASE_ORDER})
    Page<InvoiceSummaryResponse> findByPurchaseOrder_PublicId(String purchaseOrderPublicId, Pageable pageable);

}
