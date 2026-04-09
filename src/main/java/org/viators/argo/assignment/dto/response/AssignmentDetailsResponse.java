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
    String signOffPort,
    String remarks,
    String signOffRemarks,
    ResourceStatusEnum status
) {

    public static AssignmentDetailsResponse from(AssignmentT entity) {
        return new AssignmentDetailsResponse(
            entity.getPublicId(),
            entity.getSeafarer().getPublicId(),
            entity.getSeafarer().getFirstName() + " " + entity.getSeafarer().getLastName(),
            entity.getVessel().getPublicId(),
            entity.getVessel().getVesselName(),
            entity.getAssignmentRank(),
            entity.getSignOnDate(),
            entity.getExpectedSignOffDate(),
            entity.getActualSignedOffDate(),
            entity.getSignOnPort(),
            entity.getSignOffPort(),
            entity.getRemarks(),
            entity.getSignOffRemarks(),
            entity.getStatus()
        );
    }

}
