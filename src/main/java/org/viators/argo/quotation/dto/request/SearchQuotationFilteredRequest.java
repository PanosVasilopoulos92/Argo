package org.viators.argo.quotation.dto.request;

import org.viators.argo.quotation.enums.QuotationStateEnum;

import java.time.LocalDate;

public record SearchQuotationFilteredRequest(
    String quotationPublicId,
    String requisitionPublicId,
    QuotationStateEnum quotationState,
    LocalDate validUntilFrom,
    LocalDate validUntilTo,
    boolean excludeExpired
) {
}
