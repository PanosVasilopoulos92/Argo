package org.viators.argo.purchaseorder.dto.request;

import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum;
import org.viators.argo.purchaseorder.enums.PurchaseOrderTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SearchPOFilteredRequest(
    String purchaseOrderNumber,
    String supplierCompanyNameContaining,
    String sourceRequisitionPublicId,
    PurchaseOrderTypeEnum purchaseOrderType,
    PurchaseOrderStateEnum purchaseOrderState,
    CurrencyEnum currency,
    LocalDate sentAtFrom,
    LocalDate sentAtTo,
    BigDecimal totalAmountMin,
    BigDecimal totalAmountMax
) {
}
