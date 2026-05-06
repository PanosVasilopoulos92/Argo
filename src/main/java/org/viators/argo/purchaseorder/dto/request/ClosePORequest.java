package org.viators.argo.purchaseorder.dto.request;

import jakarta.validation.constraints.NotNull;

public record ClosePORequest(
    @NotNull(message = "Version is required in order to modify resource")
    Long version
) {
}
