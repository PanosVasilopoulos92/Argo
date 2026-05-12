package org.viators.argo.goodsreceipt.dto.request;

import org.viators.argo.goodsreceipt.enums.GoodsReceiptStateEnum;

import java.time.LocalDate;

public record SearchReceiptFilterRequest(
    String goodsReceiptNumber,
    String poPublicId,
    String poNumber,
    String supplierPublicId,
    GoodsReceiptStateEnum receiptState,
    LocalDate receiptDateFrom,
    LocalDate receiptDateTo,
    Boolean containsOverReceivedLine,
    Boolean containsDamagedOrWrongItem
) {
}
