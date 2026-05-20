package org.viators.argo.invoice.dto.response;

import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.invoice.InvoiceT;
import org.viators.argo.invoice.enums.InvoiceStateEnum;

import java.math.BigDecimal;
import java.util.List;

public record InvoiceDiscrepancyDetailsResponse(
    String publicId,
    String invoiceNumber,
    BigDecimal invoiceTotalAmount,
    CurrencyEnum currency,
    InvoiceStateEnum state,
    List<DiscrepancyLineDetail> lines
) {

    public static InvoiceDiscrepancyDetailsResponse from(InvoiceT invoice, List<DiscrepancyLineDetail> lines) {
        return new InvoiceDiscrepancyDetailsResponse(
            invoice.getPublicId(),
            invoice.getInvoiceNumber(),
            invoice.getTotalAmount(),
            invoice.getCurrency(),
            invoice.getInvoiceState(),
            lines
        );
    }

}
