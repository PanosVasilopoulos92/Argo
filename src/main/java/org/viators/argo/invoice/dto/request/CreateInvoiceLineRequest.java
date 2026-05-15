package org.viators.argo.invoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.viators.argo.invoice.line.InvoiceLineT;

import java.math.BigDecimal;

public record CreateInvoiceLineRequest(

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    BigDecimal unitPrice,

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    BigDecimal quantity,


    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,

    String purchaseOrderLinePublicId
) {

    public InvoiceLineT toEntity() {
        return InvoiceLineT.builder()
            .unitPrice(unitPrice)
            .quantity(quantity)
            .lineTotal(unitPrice.multiply(quantity))
            .description(description)
            .build();
    }
}
