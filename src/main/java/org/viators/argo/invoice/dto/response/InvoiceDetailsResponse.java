package org.viators.argo.invoice.dto.response;

import lombok.Builder;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.invoice.InvoiceT;
import org.viators.argo.invoice.enums.InvoiceStateEnum;
import org.viators.argo.invoice.enums.PaymentMethodEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Builder
public record InvoiceDetailsResponse(
    String invoicePublicId,
    String invoiceNumber,
    String supplierInvoiceReference,
    LocalDate invoiceDate,
    LocalDate invoiceDueDate,
    CurrencyEnum currency,
    BigDecimal totalAmount,
    InvoiceStateEnum invoiceState,
    String notes,
    String supplierPublicId,
    String supplierCompanyName,
    String poPublicId,
    String poNumber,
    Instant matchedAt,
    String matchedBy,
    Instant approvedAt,
    String approvedBy,
    Instant rejectedAt,
    String rejectedBy,
    String rejectionReason,
    Instant paidAt,
    String paidBy,
    String paymentReference,
    LocalDate paymentDate,
    PaymentMethodEnum paymentMethod,
    Instant cancelledAt,
    String cancelledBy,
    String cancellationReason,
    ResourceStatusEnum status,
    Instant createdAt,
    String createdBy,
    Instant updatedAt,
    String updatedBy,
    Long version,
    List<InvoiceLineSummaryResponse> invoiceLines
) {

    public static InvoiceDetailsResponse from(InvoiceT entity, List<InvoiceLineSummaryResponse> invoiceLines) {
        return InvoiceDetailsResponse.builder()
            .invoicePublicId(entity.getPublicId())
            .invoiceNumber(entity.getInvoiceNumber())
            .supplierInvoiceReference(entity.getSupplierInvoiceReference())
            .invoiceDate(entity.getInvoiceDate())
            .invoiceDueDate(entity.getInvoiceDueDate())
            .currency(entity.getCurrency())
            .totalAmount(entity.getTotalAmount())
            .invoiceState(entity.getInvoiceState())
            .notes(entity.getNotes())
            .supplierPublicId(entity.getSupplier().getPublicId())
            .supplierCompanyName(entity.getSupplier().getCompanyName())
            .poPublicId(entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getPublicId() : null)
            .poNumber(entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getPurchaseOrderNumber() : null)
            .matchedAt(entity.getMatchedAt())
            .matchedBy(entity.getMatchedBy())
            .approvedAt(entity.getApprovedAt())
            .approvedBy(entity.getApprovedBy())
            .rejectedAt(entity.getRejectedAt())
            .rejectedBy(entity.getRejectedBy())
            .rejectionReason(entity.getRejectionReason())
            .paidAt(entity.getRecordedPaymentAt())
            .paidBy(entity.getRecordedPaymentBy())
            .paymentReference(entity.getPaymentReference())
            .paymentDate(entity.getPaymentDate())
            .paymentMethod(entity.getPaymentMethod())
            .cancelledAt(entity.getCancelledAt())
            .cancelledBy(entity.getCancelledBy())
            .cancellationReason(entity.getCancellationReason())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .createdBy(entity.getCreatedBy())
            .updatedAt(entity.getUpdatedAt())
            .updatedBy(entity.getUpdatedBy())
            .version(entity.getVersion())
            .invoiceLines(invoiceLines)
            .build();
    }
}
