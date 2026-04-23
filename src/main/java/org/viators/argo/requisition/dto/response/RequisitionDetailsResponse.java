package org.viators.argo.requisition.dto.response;

import lombok.Builder;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.requisition.RequisitionT;
import org.viators.argo.requisition.enums.RequisitionPriorityEnum;
import org.viators.argo.requisition.enums.RequisitionStateEnum;
import org.viators.argo.requisition.enums.RequisitionTypeEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record RequisitionDetailsResponse(
    String reqPublicId,
    String reqNumber,
    RequisitionTypeEnum reqType,
    RequisitionStateEnum reqState,
    RequisitionPriorityEnum reqPriority,
    String remarks,
    LocalDate requiredByDate,
    String raisedByPublicId,
    String targetVesselPublicId,
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
    Set<RequisitionLineSummaryResponse> reqLines
) {

    public static RequisitionDetailsResponse from(RequisitionT entity) {
        return RequisitionDetailsResponse.builder()
            .reqPublicId(entity.getPublicId())
            .reqNumber(entity.getRequisitionNumber())
            .reqType(entity.getRequisitionType())
            .reqState(entity.getRequisitionState())
            .reqPriority(entity.getRequisitionPriority())
            .remarks(entity.getRemarks())
            .requiredByDate(entity.getRequiredByDate())
            .raisedByPublicId(entity.getRaisedBy().getPublicId())
            .targetVesselPublicId(entity.getTargetVessel() != null
                ? entity.getTargetVessel().getPublicId()
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
            .build();
    }
}
