package org.viators.argo.requisition.dto.response;

import org.viators.argo.person.PersonT;
import org.viators.argo.requisition.RequisitionT;
import org.viators.argo.requisition.enums.RequisitionPriorityEnum;
import org.viators.argo.requisition.enums.RequisitionStateEnum;
import org.viators.argo.requisition.enums.RequisitionTypeEnum;
import org.viators.argo.vessel.VesselT;

import java.time.Instant;

public record RequisitionSummaryResponse(
    String reqPublicId,
    String reqNumber,
    RequisitionTypeEnum requisitionType,
    String vesselName,
    String raisedByFullName,
    RequisitionStateEnum requisitionState,
    RequisitionPriorityEnum requisitionPriority,
    Instant requiredByDate,
    Instant createdAt,
    Integer numberOfLines,
    String submittedBy
) {

    public static RequisitionSummaryResponse from(RequisitionT entity) {
        PersonT raisedBy = entity.getRaisedBy();
        VesselT vessel = entity.getTargetVessel();

        return new RequisitionSummaryResponse(
            entity.getPublicId(),
            entity.getRequisitionNumber(),
            entity.getRequisitionType(),
            vessel != null ? vessel.getVesselName() : null,
            raisedBy.getLastName().concat(" ").concat(raisedBy.getFirstName()),
            entity.getRequisitionState(),
            entity.getRequisitionPriority(),
            entity.getRequiredByDate(),
            entity.getCreatedAt(),
            entity.getNumberOfLines(),
            entity.getSubmittedBy()
        );
    }
}
