package org.viators.argo.requisition.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RejectRequisitionRequest(
    @NotBlank(message = "Rejected reason is required")
    @Size(max = 500, message = "Rejected reason cannot exceed 500 characters")
    String rejectedReason,

    @NotNull(message = "Version is required for resource updates")
    Long version
) {
}
