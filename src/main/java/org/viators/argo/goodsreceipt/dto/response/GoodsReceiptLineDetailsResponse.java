package org.viators.argo.goodsreceipt.dto.response;

import lombok.Builder;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.goodsreceipt.enums.ReceiptLineFlagEnum;
import org.viators.argo.goodsreceipt.enums.ReceivedGoodsConditionEnum;
import org.viators.argo.goodsreceipt.line.GoodsReceiptLineT;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record GoodsReceiptLineDetailsResponse(
    String publicId,
    BigDecimal receivedQuantity,
    ReceivedGoodsConditionEnum receivedGoodsCondition,
    ReceiptLineFlagEnum receiptLineFlag,
    String notes,
    String goodsReceiptPublicId,
    String goodsReceiptNumber,
    ResourceStatusEnum status,
    Instant createdAt,
    String createdBy,
    Instant updatedAt,
    String updatedBy,
    Long version
) {

    public static GoodsReceiptLineDetailsResponse from(GoodsReceiptLineT entity) {
        return GoodsReceiptLineDetailsResponse.builder()
            .publicId(entity.getPublicId())
            .receivedQuantity(entity.getReceivedQuantity())
            .receivedGoodsCondition(entity.getReceivedGoodsCondition())
            .receiptLineFlag(entity.getReceiptLineFlag())
            .notes(entity.getNotes())
            .goodsReceiptPublicId(entity.getGoodsReceipt().getPublicId())
            .goodsReceiptNumber(entity.getGoodsReceipt().getGoodsReceiptNumber())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .createdBy(entity.getCreatedBy())
            .updatedAt(entity.getUpdatedAt())
            .updatedBy(entity.getUpdatedBy())
            .version(entity.getVersion())
            .build();
    }
}
