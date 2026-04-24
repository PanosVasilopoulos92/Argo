package org.viators.argo.requisition.dto.request;

import org.viators.argo.requisition.enums.RequisitionPriorityEnum;
import org.viators.argo.requisition.enums.RequisitionStateEnum;
import org.viators.argo.requisition.enums.RequisitionTypeEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

public record RequisitionSearchFilterRequest(
    RequisitionTypeEnum requisitionType,
    String vesselPublicId,
    Set<RequisitionStateEnum> states,
    String raisedByPublicId,
    RequisitionPriorityEnum priority,
    Instant createdDateFrom,
    Instant createdDateTo,
    LocalDate requiredByDateFrom,
    LocalDate requiredByDateTo,
    String reqNumber
) {
}
