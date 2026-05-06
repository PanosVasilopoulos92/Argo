package org.viators.argo.quotation.dto.response;

import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.quotation.QuotationT;
import org.viators.argo.quotation.enums.QuotationStateEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record QuotationDetailsResponse(
    String publicId,
    String requisitionLinePublicId,
    String supplierPublicId,
    BigDecimal unitPrice,
    CurrencyEnum currency,
    BigDecimal quotedQuantity,
    LocalDate validUntil,
    QuotationStateEnum quotationState,
    String notes,
    Instant acceptedAt,
    String acceptedBy,
    Instant rejectedAt,
    String rejectedBy,
    String rejectionReason,
    ResourceStatusEnum status,
    Instant createdAt,
    Instant updatedAt,
    Long version
) {

    public static QuotationDetailsResponse from(QuotationT entity) {
        return new QuotationDetailsResponse(
            entity.getPublicId(),
            entity.getReqLine().getPublicId(),
            entity.getSupplier().getPublicId(),
            entity.getUnitPrice(),
            entity.getCurrency(),
            entity.getQuotedQuantity(),
            entity.getValidUntil(),
            entity.getQuotationState(),
            entity.getNotes(),
            entity.getAcceptedAt(),
            entity.getAcceptedBy(),
            entity.getRejectedAt(),
            entity.getRejectedBy(),
            entity.getRejectionReason(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getVersion()
        );
    }
}
