package org.viators.argo.quotation.dto.request;

import jakarta.validation.constraints.NotNull;

public record AcceptQuotationRequest(
    @NotNull(message = "Version is required for resource to be updated")
    Long version
) {
}
