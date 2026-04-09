package org.viators.argo.assignment.dto.response;

import org.viators.argo.assignment.AssignmentT;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

import java.time.LocalDate;

public record CrewRosterResponse(
    String seafarerFullName,
    SeafarerRankEnum assignmentRank,
    LocalDate signOnDate,
    LocalDate expectedSignOffDate
) {

    public static CrewRosterResponse from(AssignmentT entity) {
        return new CrewRosterResponse(
            entity.getSeafarer().getFirstName().concat(" ").concat(entity.getSeafarer().getLastName()),
            entity.getAssignmentRank(),
            entity.getSignOnDate(),
            entity.getExpectedSignOffDate()
        );
    }
}
