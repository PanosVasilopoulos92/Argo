package org.viators.argo.requisition.dto.request;

import jakarta.validation.constraints.NotNull;

public record SubmitRequisitionRequest(
    @NotNull(message = "Version is required")
    Long version
) {
}
