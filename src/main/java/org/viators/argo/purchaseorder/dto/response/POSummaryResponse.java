package org.viators.argo.purchaseorder.dto.response;

import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.purchaseorder.PurchaseOrderT;
import org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum;
import org.viators.argo.purchaseorder.enums.PurchaseOrderTypeEnum;

import java.math.BigDecimal;
import java.time.Instant;

public record POSummaryResponse(
    String poPublicId,
    String poNumber,
    PurchaseOrderTypeEnum purchaseOrderType,
    PurchaseOrderStateEnum purchaseOrderState,
    CurrencyEnum currency,
    BigDecimal totalAmount,
    String supplierPublicId,
    String supplierCompanyName,
    String reqPublicId,
    String reqNumber,
    Instant sentAt,
    Instant acknowledgedAt,
    Instant createdAt,
    String createdBy
) {

    public static POSummaryResponse from(PurchaseOrderT entity) {
        return new POSummaryResponse(
            entity.getPublicId(),
            entity.getPurchaseOrderNumber(),
            entity.getPurchaseOrderType(),
            entity.getPurchaseOrderState(),
            entity.getCurrency(),
            entity.getTotalAmount(),
            entity.getSupplier().getPublicId(),
            entity.getSupplier().getCompanyName(),
            entity.getRequisition().getPublicId(),
            entity.getRequisition().getRequisitionNumber(),
            entity.getSentAt(),
            entity.getAcknowledgedAt(),
            entity.getCreatedAt(),
            entity.getCreatedBy()
        );
    }
}
