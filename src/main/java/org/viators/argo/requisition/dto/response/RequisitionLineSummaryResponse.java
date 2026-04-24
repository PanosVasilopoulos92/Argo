package org.viators.argo.requisition.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;
import org.viators.argo.requisition.RequisitionLineT;

import java.math.BigDecimal;

public record RequisitionLineSummaryResponse(
    String reqLinePublicId,
    BigDecimal quantity,
    String snapShotItemCode,
    UnitOfMeasurementEnum snapshotUnitOfMeasurement,
    ResourceStatusEnum status
) {

    public static RequisitionLineSummaryResponse from(RequisitionLineT entity) {
        return new RequisitionLineSummaryResponse(
            entity.getPublicId(),
            entity.getQuantity(),
            entity.getSnapShotItemCode(),
            entity.getSnapshotUnitOfMeasurement(),
            entity.getStatus()
        );
    }
}
