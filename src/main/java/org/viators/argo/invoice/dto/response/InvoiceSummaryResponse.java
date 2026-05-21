package org.viators.argo.invoice.dto.response;

import lombok.Builder;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.invoice.InvoiceT;
import org.viators.argo.invoice.enums.InvoiceStateEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Builder
public record InvoiceSummaryResponse(
    String invoicePublicId,
    String invoiceNumber,
    String supplierInvoiceReference,
    LocalDate invoiceDate,
    LocalDate invoiceDueDate,
    CurrencyEnum currency,
    BigDecimal totalAmount,
    InvoiceStateEnum invoiceState,
    String supplierPublicId,
    String supplierCompanyName,
    String poPublicId,
    String poNumber,
    Instant createdAt,
    String createdBy,
    Long daysUntilDue
) {

    public static InvoiceSummaryResponse from(InvoiceT entity) {
        return InvoiceSummaryResponse.builder()
            .invoicePublicId(entity.getPublicId())
            .invoiceNumber(entity.getInvoiceNumber())
            .supplierInvoiceReference(entity.getSupplierInvoiceReference())
            .invoiceDate(entity.getInvoiceDate())
            .invoiceDueDate(entity.getInvoiceDueDate())
            .currency(entity.getCurrency())
            .totalAmount(entity.getTotalAmount())
            .invoiceState(entity.getInvoiceState())
            .supplierPublicId(entity.getSupplier().getPublicId())
            .supplierCompanyName(entity.getSupplier().getCompanyName())
            .poPublicId(entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getPublicId() : null)
            .poNumber(entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getPurchaseOrderNumber() : null)
            .createdAt(entity.getCreatedAt())
            .createdBy(entity.getCreatedBy())
            .daysUntilDue(ChronoUnit.DAYS.between(LocalDate.now(), entity.getInvoiceDueDate()))
            .build();
    }
}
