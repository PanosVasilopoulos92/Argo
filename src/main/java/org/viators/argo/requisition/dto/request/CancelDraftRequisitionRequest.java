package org.viators.argo.requisition.dto.request;

import jakarta.validation.constraints.NotNull;

public record CancelDraftRequisitionRequest(
    @NotNull(message = "Version is required")
    Long version
) {
}
