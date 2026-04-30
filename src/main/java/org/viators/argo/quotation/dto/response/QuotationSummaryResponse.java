package org.viators.argo.quotation.dto.response;

import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.quotation.enums.QuotationStateEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record QuotationSummaryResponse(
    String supplierCompanyName,
    BigDecimal unitPrice,
    CurrencyEnum currency,
    BigDecimal quotedQuantity,
    BigDecimal totalAmount,
    LocalDate validUntil,
    Integer daysUntilExpiry,
    QuotationStateEnum quotationState,
    String notes,
    Instant createdAt,
    String createdBy
) {
}
