package org.viators.argo.person.seafarer.dto.request;

import jakarta.validation.constraints.Pattern;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

public record SeafarerSearchFilterRequest(
    String lastNameContaining,

    SeafarerRankEnum rank,

    @Pattern(regexp = "^[A-Z]{3}$")
    String nationality,

    boolean includeInactiveSeafarers
) {
}
