package org.viators.argo.invoice.dto.response;

import org.viators.argo.common.enums.CurrencyEnum;

import java.math.BigDecimal;

public record DiscrepanciesSummaryResponse(
    Long totalDisputedInvoices,
    Long priceMismatchCount,
    Long quantityMismatchCount,
    Long bothMismatchCount,
    Long unmatchedCount,
    BigDecimal totalDisputedAmount,
    CurrencyEnum currency
) {
    public DiscrepanciesSummaryResponse {
        if (totalDisputedAmount == null) {
            totalDisputedAmount = BigDecimal.ZERO;
        }
    }
}
