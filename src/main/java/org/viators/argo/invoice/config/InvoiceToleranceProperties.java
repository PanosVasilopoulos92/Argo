package org.viators.argo.invoice.config;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "argo.invoice.match")
@Validated
public record InvoiceToleranceProperties(
    @NotNull
    @DecimalMin("0.0")
    BigDecimal priceTolerancePercent,

    @NotNull
    @DecimalMin("0.0")
    BigDecimal quantityTolerancePercent
) {
}
