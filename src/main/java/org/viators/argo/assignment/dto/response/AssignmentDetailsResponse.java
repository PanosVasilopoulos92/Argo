package org.viators.argo.assignment.dto.response;

import org.viators.argo.assignment.AssignmentT;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

import java.time.LocalDate;

public record AssignmentDetailsResponse(
    String assignmentPublicId,
    String seafarerPublicId,
    String seafarerFullName,
    String vesselPublicId,
    String vesselName,
    SeafarerRankEnum assignmentRank,
    LocalDate signOnDate,
    LocalDate expectedSignOffDate,
    LocalDate actualSignedOffDate,
    String signOnPort,
    String remarks,
    ResourceStatusEnum status
) {
}
