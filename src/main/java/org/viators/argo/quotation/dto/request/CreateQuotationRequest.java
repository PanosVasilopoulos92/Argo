package org.viators.argo.quotation.dto.request;

import jakarta.validation.constraints.*;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.quotation.QuotationT;
import org.viators.argo.requisition.RequisitionLineT;
import org.viators.argo.supplier.SupplierT;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateQuotationRequest(

    @NotBlank(message = "Requisition line public Id is required")
    String requisitionLinePublicId,

    @NotBlank(message = "Supplier public Id is required")
    String supplierPublicId,

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be greater than 0")
    BigDecimal unitPrice,

    @NotNull(message = "Currency is required")
    CurrencyEnum currency,

    @NotNull(message = "Quoted quantity is required")
    @Positive(message = "Quoted quantity must be a positive value")
    BigDecimal quotedQuantity,

    @NotNull(message = "Valid until date is required")
    @FutureOrPresent(message = "Valid until date must be in the future")
    LocalDate validUntil,

    @Size(max = 500, message = "Notes must be at most 500 characters long")
    String notes
) {

    public QuotationT toEntity(RequisitionLineT line, SupplierT supplier) {
        return QuotationT.builder()
            .unitPrice(unitPrice)
            .currency(currency)
            .quotedQuantity(quotedQuantity)
            .validUntil(validUntil)
            .notes(notes)
            .reqLine(line)
            .supplier(supplier)
            .build();
    }
}
