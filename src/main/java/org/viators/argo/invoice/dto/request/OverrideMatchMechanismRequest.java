package org.viators.argo.invoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.viators.argo.invoice.enums.MatchStatusEnum;

import java.util.List;

public record OverrideMatchMechanismRequest(

    @NotBlank(message = "Override justification is required")
    String overrideJustification,

    @NotEmpty(message = "You must provide at least one line to override")
    List<LineOverrides> lineOverrides,

    @NotNull(message = "Version is required for updating resource")
    Long version
) {

    public record LineOverrides(
        String invoiceLinePublicId,
        String poLinePublicId,
        MatchStatusEnum forcedMatchedStatus
    ) {
    }
}
