package org.viators.argo.purchaseorder.dto.request;

import jakarta.validation.constraints.NotNull;

public record SendPOToSupplierRequest(
    @NotNull
    Long version
) {
}
