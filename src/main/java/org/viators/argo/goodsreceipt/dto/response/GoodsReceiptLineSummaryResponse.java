package org.viators.argo.goodsreceipt.dto.response;

import lombok.Builder;
import org.viators.argo.goodsreceipt.enums.ReceiptLineFlagEnum;
import org.viators.argo.goodsreceipt.enums.ReceivedGoodsConditionEnum;
import org.viators.argo.goodsreceipt.line.GoodsReceiptLineT;

import java.math.BigDecimal;

@Builder
public record GoodsReceiptLineSummaryResponse(
    String publicId,
    BigDecimal receivedQuantity,
    ReceivedGoodsConditionEnum receivedGoodsCondition,
    ReceiptLineFlagEnum receiptLineFlag,
    String notes
) {

    public static GoodsReceiptLineSummaryResponse from(GoodsReceiptLineT entity) {
        return GoodsReceiptLineSummaryResponse.builder()
            .publicId(entity.getPublicId())
            .receivedQuantity(entity.getReceivedQuantity())
            .receivedGoodsCondition(entity.getReceivedGoodsCondition())
            .receiptLineFlag(entity.getReceiptLineFlag())
            .notes(entity.getNotes())
            .build();
    }
}
