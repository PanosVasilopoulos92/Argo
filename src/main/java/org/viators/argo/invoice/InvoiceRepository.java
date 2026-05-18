package org.viators.argo.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface InvoiceRepository extends JpaRepository<InvoiceT, Long> {

    @Query("""
           select i from InvoiceT i
           join fetch i.invoiceLines il
           where i.publicId = :invPublicId
           """)
    Optional<InvoiceT> findByPublicIdWithLines(@Param("invPublicId") String invPublicId);

    boolean existsBySupplierInvoiceReferenceAndSupplier_Id(String paymentReference, Long supplierId);

    boolean existsByPublicIdAndPurchaseOrderIsNull(String id);
}
