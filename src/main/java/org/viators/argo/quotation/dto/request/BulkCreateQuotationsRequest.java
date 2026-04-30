package org.viators.argo.quotation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.quotation.QuotationT;
import org.viators.argo.supplier.SupplierT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BulkCreateQuotationsRequest(

    @NotBlank(message = "Supplier public Id is required")
    String supplierPublicId,

    @NotNull(message = "Currency is required")
    CurrencyEnum currency,

    @NotNull(message = "Valid until date is required")
    @Future(message = "Valid until date must be in the future")
    LocalDate validUntil,

    @Size(max = 500, message = "Notes must be at most 500 characters long")
    String notes,

    @NotEmpty(message = "Bulk quotation must include at least one line quotation")
    @Size(max = 100, message = "Max number of line quotations is 100")
    @Valid
    List<LineQuotation> lineQuotations
) {

    public QuotationT toEntity(SupplierT supplier, LineQuotation lineQuotation) {
        return QuotationT.builder()
            .unitPrice(lineQuotation.unitPrice)
            .currency(currency)
            .quotedQuantity(lineQuotation.quotedQuantity)
            .validUntil(validUntil)
            .notes(notes)
            .supplier(supplier)
            .build();
    }

    public record LineQuotation(
        @NotBlank(message = "Requisition line public Id is required")
        String requisitionLinePublicId,

        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be greater than 0")
        BigDecimal unitPrice,

        @NotNull(message = "Quoted quantity is required")
        @Positive(message = "Quoted quantity must be a positive value")
        BigDecimal quotedQuantity
    ) {
    }
}
