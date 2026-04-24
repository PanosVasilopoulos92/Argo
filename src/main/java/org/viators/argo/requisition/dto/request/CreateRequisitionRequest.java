package org.viators.argo.requisition.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.viators.argo.requisition.RequisitionT;
import org.viators.argo.requisition.enums.RequisitionPriorityEnum;
import org.viators.argo.requisition.enums.RequisitionTypeEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

public record CreateRequisitionRequest(
    @NotNull(message = "'Raised by' field is required")
    String raisedByPublicId,

    @NotNull(message = "Requisition type is required")
    RequisitionTypeEnum requisitionType,

    RequisitionPriorityEnum requisitionPriority,

    @Size(max = 500, message = "Remarks must be at most 500 characters long")
    String remarks,

    @FutureOrPresent(message = "Required-by date cannot be in the past")
    LocalDate requiredByDate,

    @Size(max = 100, message = "Target vessel public id must be at most 100 characters long")
    String targetVesselPublicId,

    @NotEmpty(message = "At least one requisition line is required")
    @Valid
    Set<CreateRequisitionLineRequest> lineRequests
) {

    public RequisitionT toEntity() {
        return RequisitionT.builder()
            .requisitionType(requisitionType)
            .requisitionPriority(requisitionPriority)
            .remarks(remarks)
            .requiredByDate(requiredByDate)
            .build();
    }
}
