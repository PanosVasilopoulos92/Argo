package org.viators.argo.invoice.dto.response;

import lombok.Builder;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.invoice.enums.MatchStatusEnum;
import org.viators.argo.invoice.line.InvoiceLineT;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record InvoiceLineDetailsResponse(
    String publicId,
    String lineDescription,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal,
    MatchStatusEnum matchStatus,
    BigDecimal priceVariance,
    BigDecimal quantityVariance,
    String invoicePublicId,
    String invoiceNumber,
    String poLinePublicId,
    String snapShotItemCode,
    String snapShotItemName,
    ResourceStatusEnum status,
    Instant createdAt,
    String createdBy,
    Instant updatedAt,
    String updatedBy,
    Long version
) {

    public static InvoiceLineDetailsResponse from(InvoiceLineT entity) {
        return InvoiceLineDetailsResponse.builder()
            .publicId(entity.getPublicId())
            .lineDescription(entity.getDescription())
            .quantity(entity.getQuantity())
            .unitPrice(entity.getUnitPrice())
            .lineTotal(entity.getLineTotal())
            .matchStatus(entity.getMatchStatus())
            .priceVariance(entity.getPriceVariance())
            .quantityVariance(entity.getQuantityVariance())
            .invoicePublicId(entity.getInvoice().getPublicId())
            .invoiceNumber(entity.getInvoice().getInvoiceNumber())
            .poLinePublicId(entity.getPoLine().getPublicId())
            .snapShotItemCode(entity.getPoLine().getSnapShotItemCode())
            .snapShotItemName(entity.getPoLine().getSnapShotItemName())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .createdBy(entity.getCreatedBy())
            .updatedAt(entity.getUpdatedAt())
            .updatedBy(entity.getUpdatedBy())
            .version(entity.getVersion())
            .build();
    }
}
