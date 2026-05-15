package org.viators.argo.invoice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.invoice.InvoiceT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateInvoiceRequest(

    @NotBlank(message = "Supplier publicId is required")
    String supplierPublicId,

    @Size(max = 100, message = "Supplier invoice reference cannot exceed 100 characters")
    String supplierInvoiceReference,

    @NotNull(message = "Invoice date is required")
    @PastOrPresent(message = "Invoice date must be today or a past date")
    LocalDate invoiceDate,

    LocalDate invoiceDueDate,

    @NotNull(message = "Currency is required")
    CurrencyEnum currency,

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    BigDecimal totalAmount,

    String purchaseOrderPublicId,

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes,

    @NotEmpty(message = "At least one invoice line must be provided")
    @Valid
    List<CreateInvoiceLineRequest> invoiceLines
) {

    public InvoiceT toEntity() {
        return InvoiceT.builder()
            .supplierInvoiceReference(supplierInvoiceReference)
            .invoiceDate(invoiceDate)
            .invoiceDueDate(invoiceDueDate)
            .currency(currency)
            .totalAmount(totalAmount)
            .notes(notes)
            .build();

    }
}
