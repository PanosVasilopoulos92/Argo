package org.viators.argo.invoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record AssociateInvoiceToPORequest(
    @NotBlank(message = "Purchase order public Id is required")
    String purchaseOrderPublicId,

    @NotNull(message = "Version is required for updating resource")
    Long version,

    @NotEmpty(message = "You must provide at least one association")
    Map<String, String> lineAssociations
) {
}
