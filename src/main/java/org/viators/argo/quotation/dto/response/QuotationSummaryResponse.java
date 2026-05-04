package org.viators.argo.quotation.dto.response;

import lombok.Builder;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;
import org.viators.argo.quotation.QuotationT;
import org.viators.argo.quotation.enums.QuotationStateEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Builder
public record QuotationSummaryResponse(
    String publicId,
    String linePublicId,
    String supplierCompanyName,
    BigDecimal unitPrice,
    CurrencyEnum currency,
    BigDecimal quotedQuantity,
    UnitOfMeasurementEnum unitOfMeasurementEnum,
    BigDecimal totalAmount,
    LocalDate validUntil,
    Long daysUntilExpiry,
    QuotationStateEnum quotationState,
    String notes,
    Instant createdAt,
    String createdBy
) {

    public static QuotationSummaryResponse from(QuotationT entity) {
        return QuotationSummaryResponse.builder()
            .publicId(entity.getPublicId())
            .linePublicId(entity.getLine().getPublicId())
            .supplierCompanyName(entity.getSupplier().getCompanyName())
            .unitPrice(entity.getUnitPrice())
            .currency(entity.getCurrency())
            .quotedQuantity(entity.getQuotedQuantity())
            .unitOfMeasurementEnum(entity.getLine().getSnapshotUnitOfMeasurement())
            .totalAmount(entity.getUnitPrice().multiply(entity.getQuotedQuantity()).setScale(2, RoundingMode.HALF_UP))
            .validUntil(entity.getValidUntil())
            .daysUntilExpiry(ChronoUnit.DAYS.between(LocalDate.now(), entity.getValidUntil()))
            .quotationState(entity.getQuotationState())
            .notes(entity.getNotes())
            .createdAt(entity.getCreatedAt())
            .createdBy(entity.getCreatedBy())
            .build();
    }
}
