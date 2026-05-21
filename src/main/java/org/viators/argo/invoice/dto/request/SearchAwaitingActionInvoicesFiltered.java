package org.viators.argo.invoice.dto.request;

import jakarta.validation.constraints.Positive;
import org.springframework.util.StringUtils;
import org.viators.argo.common.enums.CurrencyEnum;

public record SearchAwaitingActionInvoicesFiltered(
    String supplierPublicId,

    CurrencyEnum currency,

    @Positive(message = "Due days must be a positive number")
    Long dueWithinDays
) {

    public boolean isEmpty() {
        return StringUtils.hasText(supplierPublicId) &&
            currency == null &&
            dueWithinDays == null;
    }
}
