package org.viators.argo.invoice.dto.response;

import org.viators.argo.invoice.enums.MatchStatusEnum;
import org.viators.argo.invoice.line.InvoiceLineT;

import java.math.BigDecimal;

public record DiscrepancyLineDetail(
    String linePublicId,
    String poLinePublicId,
    String description,
    MatchStatusEnum matchStatus,
    BigDecimal receiptQuantity,
    BigDecimal invoiceLineTotalAmount,
    String explanation
) {

    public static DiscrepancyLineDetail from(InvoiceLineT invoiceLine, BigDecimal receivedQuantity, String explanation) {
        return new DiscrepancyLineDetail(
            invoiceLine.getPublicId(),
            invoiceLine.getPoLine().getPublicId(),
            invoiceLine.getDescription(),
            invoiceLine.getMatchStatus(),
            receivedQuantity,
            invoiceLine.getLineTotal(),
            explanation
        );
    }

}