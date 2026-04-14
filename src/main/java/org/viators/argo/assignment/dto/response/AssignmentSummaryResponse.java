package org.viators.argo.assignment.dto.response;

import org.viators.argo.assignment.AssignmentStateEnum;

import java.time.LocalDate;

public record AssignmentSummaryResponse(
    String assignmentPublicId,
    String seafarerPublicId,
    String vesselPublicId,
    LocalDate signOnDate,
    LocalDate actualSignedOffDate,
    AssignmentStateEnum assignmentState
) {
}
