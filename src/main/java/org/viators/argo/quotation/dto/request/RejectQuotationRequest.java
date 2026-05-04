package org.viators.argo.quotation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RejectQuotationRequest(

    @NotBlank(message = "Rejecting a quotation needs a recorded reason")
    @Size(max = 400, message = "Reason of rejection can be at most 400 characters long")
    String rejectionReason,

    @NotNull(message = "Version is required for resource to be updated")
    Long version
) {
}
