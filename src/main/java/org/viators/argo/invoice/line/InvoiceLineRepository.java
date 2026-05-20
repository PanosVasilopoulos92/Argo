package org.viators.argo.invoice.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface InvoiceLineRepository extends JpaRepository<InvoiceLineT, Long> {

    @Query("""
           select il from InvoiceLineT il
           join il.invoice i
           left join fetch il.poLine pl
           left join fetch pl.goodsReceiptLines grl
           where i.publicId = :invoicePublicId
           and il.matchStatus <> org.viators.argo.invoice.enums.MatchStatusEnum.MATCHED
           """)
    Set<InvoiceLineT> findByInvoiceWithPOLineAndReceipts(@Param("invoicePublicId") String invoicePublicId);
}
