package org.viators.argo.goodsreceipt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.viators.argo.goodsreceipt.enums.ReceivedGoodsConditionEnum;

import java.math.BigDecimal;

public record GoodsReceiptLinesRequest(
    @NotBlank(message = "PO line publicId is required")
    String poLinePublicId,

    @NotNull(message = "Received quantity is required")
    @Positive(message = "Received quantity must be positive")
    BigDecimal receivedQuantity,

    ReceivedGoodsConditionEnum receivedGoodsCondition,

    @Size(max = 400, message = "Notes cannot exceed 400 characters")
    String notes
) {
}
