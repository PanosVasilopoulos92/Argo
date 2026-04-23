package org.viators.argo.requisition.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApproveRequisitionRequest(
    @Size(max = 500, message = "Approval remarks cannot exceed 500 characters")
    String approvalRemarks,

    @NotNull(message = "Version is required for resource updates")
    Long version
) {
}
