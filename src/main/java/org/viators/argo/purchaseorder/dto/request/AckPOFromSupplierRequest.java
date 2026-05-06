package org.viators.argo.purchaseorder.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AckPOFromSupplierRequest(

    @Size(max = 100, message = "Supplier Acknowledge reference cannot exceed 100 characters")
    String supplierAckReference,

    @NotNull(message = "Version is required in order to modify resource")
    Long version
) {

}