package org.viators.argo.invoice.dto.response;

import lombok.Builder;
import org.viators.argo.invoice.enums.MatchStatusEnum;
import org.viators.argo.invoice.line.InvoiceLineT;

import java.math.BigDecimal;

@Builder
public record InvoiceLineSummaryResponse(
    String publicId,
    String lineDescription,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal,
    MatchStatusEnum matchStatus,
    String poLinePublicId,
    String snapShotItemCode,
    String snapShotItemName
) {

    public static InvoiceLineSummaryResponse from(InvoiceLineT entity) {
        return InvoiceLineSummaryResponse.builder()
            .publicId(entity.getPublicId())
            .lineDescription(entity.getDescription())
            .quantity(entity.getQuantity())
            .unitPrice(entity.getUnitPrice())
            .lineTotal(entity.getLineTotal())
            .matchStatus(entity.getMatchStatus())
            .poLinePublicId(entity.getPoLine().getPublicId())
            .snapShotItemCode(entity.getPoLine().getSnapShotItemCode())
            .snapShotItemName(entity.getPoLine().getSnapShotItemName())
            .build();
    }
}
