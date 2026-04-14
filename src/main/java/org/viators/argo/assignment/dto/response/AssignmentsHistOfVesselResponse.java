package org.viators.argo.assignment.dto.response;

import org.viators.argo.assignment.AssignmentStateEnum;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

import java.time.LocalDate;

public record AssignmentsHistOfVesselResponse(
    String seafarerPublicId,
    String seafarerFullName,
    SeafarerRankEnum assignmentRank,
    LocalDate signOnDate,
    LocalDate actualSignOffDate,
    String signOnPort,
    String signOffPort,
    AssignmentStateEnum assignmentState
) {
}
