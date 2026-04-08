package org.viators.argo.assignment.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;

import java.time.LocalDate;

public record AssignmentSummaryResponse(
    String assignmentPublicId,
    String seafarerPublicId,
    String vesselPublicId,
    LocalDate signOnDate,
    LocalDate actualSignedOffDate,
    ResourceStatusEnum status
) {
}
