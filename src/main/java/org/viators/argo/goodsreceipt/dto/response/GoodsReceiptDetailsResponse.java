package org.viators.argo.goodsreceipt.dto.response;

import lombok.Builder;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.goodsreceipt.GoodsReceiptT;
import org.viators.argo.goodsreceipt.enums.GoodsReceiptStateEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Builder
public record GoodsReceiptDetailsResponse(
    String goodsReceiptPublicId,
    String goodsReceiptNumber,
    LocalDate receiptDate,
    GoodsReceiptStateEnum receiptState,
    String deliveryNotes,
    String poPublicId,
    String poNumber,
    Instant cancelledAt,
    String cancelledBy,
    String cancellationReason,
    ResourceStatusEnum status,
    Instant createdAt,
    String createdBy,
    Instant updatedAt,
    String updatedBy,
    Long version,
    List<GoodsReceiptLineSummaryResponse> goodsReceiptLines
) {

    public static GoodsReceiptDetailsResponse from(GoodsReceiptT entity, List<GoodsReceiptLineSummaryResponse> goodsReceiptLines) {
        return GoodsReceiptDetailsResponse.builder()
            .goodsReceiptPublicId(entity.getPublicId())
            .goodsReceiptNumber(entity.getGoodsReceiptNumber())
            .receiptDate(entity.getReceiptDate())
            .receiptState(entity.getReceiptState())
            .deliveryNotes(entity.getDeliveryNotes())
            .poPublicId(entity.getPurchaseOrder().getPublicId())
            .poNumber(entity.getPurchaseOrder().getPurchaseOrderNumber())
            .cancelledAt(entity.getCancelledAt())
            .cancelledBy(entity.getCancelledBy())
            .cancellationReason(entity.getCancellationReason())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .createdBy(entity.getCreatedBy())
            .updatedAt(entity.getUpdatedAt())
            .updatedBy(entity.getUpdatedBy())
            .version(entity.getVersion())
            .goodsReceiptLines(goodsReceiptLines)
            .build();
    }
}
