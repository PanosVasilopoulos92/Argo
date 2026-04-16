package org.viators.argo.assignment.dto.projection;

import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

public record ActiveAssignmentInfo(
    String vesselName,
    SeafarerRankEnum rank
) {
}
