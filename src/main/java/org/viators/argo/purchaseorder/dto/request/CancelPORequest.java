package org.viators.argo.purchaseorder.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CancelPORequest(
    @NotBlank(message = "Reason of cancellation is required")
    @Size(max = 500, message = "Cancellation reason cannot exceed 500 characters")
    String cancellationReason,

    @NotNull(message = "Version is required in order to modify resource")
    Long version
) {
}
