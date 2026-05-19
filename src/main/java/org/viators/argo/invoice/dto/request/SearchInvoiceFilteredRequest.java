package org.viators.argo.invoice.dto.request;

import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.invoice.enums.InvoiceStateEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SearchInvoiceFilteredRequest(
    String invoiceNumber,
    String supplierInvoiceReference,
    String supplierPublicId,
    String purchaseOrderPublicId,
    InvoiceStateEnum invoiceState,
    LocalDate invoiceDateFrom,
    LocalDate invoiceDateTo,
    LocalDate invoiceDueDateFrom,
    LocalDate invoiceDueDateTo,
    BigDecimal totalAmountMin,
    BigDecimal totalAmountMax,
    CurrencyEnum currency,
    boolean hasUnmatchedLines,
    boolean hasPriceDiscrepancy,
    boolean hasQuantityDiscrepancy
) {
}
