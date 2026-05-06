package org.viators.argo.purchaseorder.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.purchaseorder.enums.PurchaseOrderTypeEnum;

import java.util.Set;

public record CreatePORequest(

    @NotNull(message = "Purchase order type is required")
    PurchaseOrderTypeEnum purchaseOrderType,

    @Size(max = 500, message = "Justification notes cannot exceed 500 characters")
    String justificationNotes,

    @NotEmpty(message = "At least one quotation must be provided")
    Set<String> quotationPublicIds
) {
}
