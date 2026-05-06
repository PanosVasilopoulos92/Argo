package org.viators.argo.purchaseorder.dto.response;

import lombok.Builder;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.purchaseorder.PurchaseOrderT;
import org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum;
import org.viators.argo.purchaseorder.enums.PurchaseOrderTypeEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record PODetailsResponse(
    String poPublicId,
    String poNumber,
    PurchaseOrderTypeEnum purchaseOrderType,
    PurchaseOrderStateEnum purchaseOrderState,
    String urgentJustificationNotes,
    CurrencyEnum currency,
    BigDecimal totalAmount,
    String supplierPublicId,
    String supplierCompanyName,
    String reqPublicId,
    Instant sentAt,
    Instant acknowledgedAt,
    String acknowledgedBy,
    String supplierAckReference,
    Instant closedAt,
    String closedBy,
    Instant cancelledAt,
    String cancelledBy,
    String cancellationReason,
    ResourceStatusEnum status,
    Instant createdAt,
    String createdBy,
    Instant updatedAt,
    String updatedBy,
    Long version,
    List<POLineSummaryResponse> poLines
) {

    public static PODetailsResponse from(PurchaseOrderT entity, List<POLineSummaryResponse> poLines) {
        return PODetailsResponse.builder()
            .poPublicId(entity.getPublicId())
            .poNumber(entity.getPurchaseOrderNumber())
            .purchaseOrderType(entity.getPurchaseOrderType())
            .purchaseOrderState(entity.getPurchaseOrderState())
            .urgentJustificationNotes(entity.getJustificationNotes())
            .currency(entity.getCurrency())
            .totalAmount(entity.getTotalAmount())
            .supplierPublicId(entity.getSupplier().getPublicId())
            .supplierCompanyName(entity.getSupplier().getCompanyName())
            .reqPublicId(entity.getRequisition().getPublicId())
            .sentAt(entity.getSentAt())
            .acknowledgedAt(entity.getAcknowledgedAt())
            .acknowledgedBy(entity.getAcknowledgedBy())
            .supplierAckReference(entity.getSupplierAckReference())
            .closedAt(entity.getClosedAt())
            .closedBy(entity.getClosedBy())
            .cancelledAt(entity.getCancelledAt())
            .cancelledBy(entity.getCancelledBy())
            .cancellationReason(entity.getCancellationReason())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .createdBy(entity.getCreatedBy())
            .updatedAt(entity.getUpdatedAt())
            .updatedBy(entity.getUpdatedBy())
            .version(entity.getVersion())
            .poLines(poLines)
            .build();
    }
}
