package org.viators.argo.purchaseorder.dto.response;

import lombok.Builder;
import org.viators.argo.purchaseorder.line.PurchaseOrderLineT;

import java.math.BigDecimal;

@Builder
public record POLineSummaryResponse(
    String publicId,
    String snapShotItemCode,
    String snapShotItemName,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal
) {

    public static POLineSummaryResponse from(PurchaseOrderLineT entity) {
        return POLineSummaryResponse.builder()
            .publicId(entity.getPublicId())
            .snapShotItemCode(entity.getSnapShotItemCode())
            .snapShotItemName(entity.getSnapShotItemName())
            .quantity(entity.getQuantity())
            .unitPrice(entity.getUnitPrice())
            .lineTotal(entity.getLineTotal())
            .build();
    }
}
