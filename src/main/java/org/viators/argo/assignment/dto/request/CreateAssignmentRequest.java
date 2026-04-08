package org.viators.argo.assignment.dto.request;

import jakarta.validation.constraints.*;
import org.viators.argo.assignment.AssignmentT;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

import java.time.LocalDate;

public record CreateAssignmentRequest(
    @NotBlank(message = "Seafarer public id is required")
    String seafarerPublicId,

    @NotBlank(message = "Vessel public id is required")
    String vesselPublicId,

    @NotNull(message = "Assignment rank is required")
    SeafarerRankEnum assignmentRank,

    @NotNull(message = "Sign on date is required")
    @FutureOrPresent(message = "Sign on date must be today or in the future")
    LocalDate signOnDate,

    @NotNull(message = "Expected sign off date is required")
    @Future(message = "Expected sign off date must be in the future")
    LocalDate expectedSignOffDate,

    @NotBlank(message = "Sign on port is required")
    @Size(max = 100, message = "Sign on port must be at most 100 characters long")
    String signOnPort,

    String remarks
) {

    public AssignmentT toEntity() {
        return AssignmentT.builder()
            .assignmentRank(assignmentRank)
            .signOnDate(signOnDate)
            .expectedSignOffDate(expectedSignOffDate)
            .signOnPort(signOnPort)
            .remarks(remarks)
            .build();
    }
}
