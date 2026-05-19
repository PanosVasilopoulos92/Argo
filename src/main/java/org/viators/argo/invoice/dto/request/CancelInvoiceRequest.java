package org.viators.argo.invoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CancelInvoiceRequest(
    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 400, message = "Cancellation reason cannot exceed 400 characters")
    String cancellationReason,

    @NotNull(message = "Version is required for updating resource")
    Long version
) {
}
