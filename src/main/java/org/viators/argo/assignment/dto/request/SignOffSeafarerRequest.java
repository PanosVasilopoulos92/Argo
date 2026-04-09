package org.viators.argo.assignment.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.viators.argo.assignment.AssignmentT;

import java.time.LocalDate;

public record SignOffSeafarerRequest(

    @Size(max = 100, message = "Sign off port name cannot exceed 100 characters")
    String signOffPort,

    @NotNull(message = "Sign off date is required")
    @FutureOrPresent(message = "Sign off date must be in present or in future")
    LocalDate actualSignedOffDate,

    @Size(max = 500, message = "Sign off remarks cannot exceed 500 characters")
    String signOffRemarks
) {

    public void signOffSeafarer(AssignmentT entity) {
        entity.setSignOffPort(signOffPort);
        entity.setActualSignedOffDate(actualSignedOffDate);
        entity.setSignOffRemarks(signOffRemarks);
    }
}
