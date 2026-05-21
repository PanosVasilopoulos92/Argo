package org.viators.argo.invoice.dto.request;

import jakarta.validation.constraints.*;
import org.viators.argo.invoice.enums.PaymentMethodEnum;

import java.time.LocalDate;

public record RecordPaymentRequest(
    @NotBlank(message = "Payment reference is required")
    @Size(max = 100, message = "Payment reference cannot exceed 100 characters")
    String paymentReference,

    @NotNull(message = "Payment date is required")
    @PastOrPresent(message = "Payment date must be in present or past date")
    LocalDate paymentDate,

    @NotNull(message = "Payment method is required")
    PaymentMethodEnum paymentMethod,

    @NotNull(message = "Version is required for updating resource")
    Long version
) {
}
