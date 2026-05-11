package org.viators.argo.goodsreceipt.dto.response;

import org.viators.argo.goodsreceipt.GoodsReceiptT;
import org.viators.argo.goodsreceipt.enums.GoodsReceiptStateEnum;

import java.time.Instant;
import java.time.LocalDate;

public record GoodsReceiptSummaryResponse(
    String goodsReceiptPublicId,
    String goodsReceiptNumber,
    LocalDate receiptDate,
    GoodsReceiptStateEnum receiptState,
    String poPublicId,
    String poNumber,
    Instant createdAt,
    String createdBy
) {

    public static GoodsReceiptSummaryResponse from(GoodsReceiptT entity) {
        return new GoodsReceiptSummaryResponse(
            entity.getPublicId(),
            entity.getGoodsReceiptNumber(),
            entity.getReceiptDate(),
            entity.getReceiptState(),
            entity.getPurchaseOrder().getPublicId(),
            entity.getPurchaseOrder().getPurchaseOrderNumber(),
            entity.getCreatedAt(),
            entity.getCreatedBy()
        );
    }
}
