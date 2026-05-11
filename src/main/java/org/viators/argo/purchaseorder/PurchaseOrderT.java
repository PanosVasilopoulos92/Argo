package org.viators.argo.purchaseorder;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.goodsreceipt.GoodsReceiptT;
import org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum;
import org.viators.argo.purchaseorder.enums.PurchaseOrderTypeEnum;
import org.viators.argo.purchaseorder.line.PurchaseOrderLineT;
import org.viators.argo.requisition.RequisitionT;
import org.viators.argo.supplier.SupplierT;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseOrderT extends BaseEntity {

    @Column(name = "purchase_order_number", nullable = false, unique = true, updatable = false)
    private String purchaseOrderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, updatable = false)
    @Builder.Default
    private PurchaseOrderTypeEnum purchaseOrderType = PurchaseOrderTypeEnum.STANDARD;

    @Column(name = "justification_notes", length = 500)
    private String justificationNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_state", nullable = false)
    @Builder.Default
    private PurchaseOrderStateEnum purchaseOrderState = PurchaseOrderStateEnum.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyEnum currency;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "acknowledged_by")
    private String acknowledgedBy;

    @Column(name = "supplier_ack_reference", length = 100)
    private String supplierAckReference;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "closed_by")
    private String closedBy;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id", nullable = false)
    private SupplierT supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requisition_id", referencedColumnName = "id", nullable = false)
    private RequisitionT requisition;

    @OneToMany(mappedBy = "purchaseOrder", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Builder.Default
    private Set<PurchaseOrderLineT> poLines = new HashSet<>();

    @OneToMany(mappedBy = "purchaseOrder")
    @Builder.Default
    private Set<GoodsReceiptT> goodsReceipts = new HashSet<>();

    // Helper methods
    public void addPOLine(PurchaseOrderLineT poLine) {
        if (poLine != null) {
            this.poLines.add(poLine);
            poLine.setPurchaseOrder(this);
        }
    }
}
