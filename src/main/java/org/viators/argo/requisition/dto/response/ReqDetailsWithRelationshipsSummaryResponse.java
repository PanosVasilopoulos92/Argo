package org.viators.argo.requisition.dto.response;

import lombok.Builder;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.person.PersonSummaryResponse;
import org.viators.argo.person.PersonT;
import org.viators.argo.requisition.RequisitionT;
import org.viators.argo.requisition.enums.RequisitionPriorityEnum;
import org.viators.argo.requisition.enums.RequisitionStateEnum;
import org.viators.argo.requisition.enums.RequisitionTypeEnum;
import org.viators.argo.vessel.VesselT;
import org.viators.argo.vessel.dto.response.VesselSummaryResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record ReqDetailsWithRelationshipsSummaryResponse(
    String reqPublicId,
    String reqNumber,
    RequisitionTypeEnum reqType,
    RequisitionStateEnum reqState,
    RequisitionPriorityEnum reqPriority,
    String remarks,
    LocalDate requiredByDate,
    PersonSummaryResponse raisedByPersonSummary,
    VesselSummaryResponse targetVesselSummary,
    Instant submittedAt,
    String submittedBy,
    Instant approvedAt,
    String approvedBy,
    String approvalRemarks,
    Instant rejectedAt,
    String rejectedBy,
    String rejectedReason,
    Instant cancelledAt,
    String cancelledBy,
    ResourceStatusEnum status,
    Instant createdAt,
    String createdBy,
    Set<RequisitionLineSummaryResponse> reqLines,
    Long version
) {

    public static ReqDetailsWithRelationshipsSummaryResponse from(RequisitionT entity) {
        PersonT raisedBy = entity.getRaisedBy();
        VesselT targetVessel = entity.getTargetVessel();

        return ReqDetailsWithRelationshipsSummaryResponse.builder()
            .reqPublicId(entity.getPublicId())
            .reqNumber(entity.getRequisitionNumber())
            .reqType(entity.getRequisitionType())
            .reqState(entity.getRequisitionState())
            .reqPriority(entity.getRequisitionPriority())
            .remarks(entity.getRemarks())
            .requiredByDate(entity.getRequiredByDate())
            .raisedByPersonSummary(PersonSummaryResponse.from(raisedBy))
            .targetVesselSummary(targetVessel != null
                ? VesselSummaryResponse.from(targetVessel)
                : null
            )
            .submittedAt(entity.getSubmittedAt())
            .submittedBy(entity.getSubmittedBy())
            .approvedAt(entity.getApprovedAt())
            .approvedBy(entity.getApprovedBy())
            .approvalRemarks(entity.getApprovalRemarks())
            .rejectedAt(entity.getRejectedAt())
            .rejectedBy(entity.getRejectedBy())
            .rejectedReason(entity.getRejectedReason())
            .cancelledAt(entity.getCancelledAt())
            .cancelledBy(entity.getCancelledBy())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .createdBy(entity.getCreatedBy())
            .reqLines(entity.getLines().stream()
                .map(RequisitionLineSummaryResponse::from)
                .collect(Collectors.toSet()))
            .version(entity.getVersion())
            .build();
    }
}
