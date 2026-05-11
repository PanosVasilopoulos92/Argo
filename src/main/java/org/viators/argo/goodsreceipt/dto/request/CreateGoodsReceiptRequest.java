package org.viators.argo.goodsreceipt.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record CreateGoodsReceiptRequest(
    @NotBlank(message = "PO's publicId is required")
    String poPublicId,

    @NotNull(message = "Receipt date is required")
    @PastOrPresent(message = "Receipt date must be today or past date")
    LocalDate receiptDate,

    @Size(max = 500, message = "Delivery notes cannot exceed 500 characters")
    String deliveryNotes,

    @NotEmpty(message = "Receipt lines are required")
    List<GoodsReceiptLinesRequest> receiptLines
) {
}
