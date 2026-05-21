package org.viators.argo.invoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RejectInvoiceRequest(
    @NotBlank(message = "Rejection reason is required")
    @Size(max = 500, message = "Rejection reason cannot exceed 500 characters")
    String rejectionReason,

    @NotNull(message = "Version is required for updating resource")
    Long version
) {
}
