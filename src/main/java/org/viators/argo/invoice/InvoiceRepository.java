package org.viators.argo.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface InvoiceRepository extends JpaRepository<InvoiceT, Long> {

    boolean existsByPaymentReferenceAndSupplier_Id(String paymentReference, Long supplierId);
}
