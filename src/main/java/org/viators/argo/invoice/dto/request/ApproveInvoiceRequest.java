package org.viators.argo.invoice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApproveInvoiceRequest(
    @Size(max = 500, message = "Approval notes cannot exceed 500 characters")
    String approvalNotes,

    @NotNull(message = "Version is required for updating resource")
    Long version
) {
}
