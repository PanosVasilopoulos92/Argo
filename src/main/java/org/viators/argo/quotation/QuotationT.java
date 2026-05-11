package org.viators.argo.quotation;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.purchaseorder.line.PurchaseOrderLineT;
import org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum;
import org.viators.argo.quotation.enums.QuotationStateEnum;
import org.viators.argo.requisition.RequisitionLineT;
import org.viators.argo.supplier.SupplierT;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "quotations")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class QuotationT extends BaseEntity {

    @Column(name = "price", nullable = false)
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyEnum currency;

    @Column(name = "quoted_quantity", nullable = false)
    private BigDecimal quotedQuantity;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(name = "state", nullable = false)
    @Builder.Default
    private QuotationStateEnum quotationState = QuotationStateEnum.RECEIVED;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "accepted_by")
    private String acceptedBy;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Column(name = "rejected_by")
    private String rejectedBy;

    @Column(name = "rejection_reason", length = 400)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private RequisitionLineT reqLine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private SupplierT supplier;

    @OneToOne(mappedBy = "quotation")
    private PurchaseOrderLineT purchaseOrderLine;

    // Helper method
    public boolean hasPOLine() {
        return purchaseOrderLine != null
            && purchaseOrderLine.getPurchaseOrder().getPurchaseOrderState() != PurchaseOrderStateEnum.CANCELLED;
    }
}
