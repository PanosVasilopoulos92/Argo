package org.viators.argo.goodsreceipt;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.goodsreceipt.enums.GoodsReceiptStateEnum;
import org.viators.argo.goodsreceipt.line.GoodsReceiptLineT;
import org.viators.argo.purchaseorder.PurchaseOrderT;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "goods_receipts")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GoodsReceiptT extends BaseEntity {

    @Column(name = "goods_receipt_number", nullable = false, updatable = false)
    private String goodsReceiptNumber;

    @Column(name = "receipt_date", nullable = false)
    private LocalDate receiptDate;

    @Column(name = "delivery_notes", length = 500)
    private String deliveryNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @Builder.Default
    private GoodsReceiptStateEnum receiptState = GoodsReceiptStateEnum.RECORDED;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @Column(name = "cancellation_reason", length = 400)
    private String cancellationReason;

    @OneToMany(mappedBy = "goodsReceipt", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<GoodsReceiptLineT> goodsReceiptLines = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", referencedColumnName = "id", nullable = false)
    private PurchaseOrderT purchaseOrder;

    // Helper methods
    public void addReceiptLine(GoodsReceiptLineT goodsReceiptLine) {
        if (goodsReceiptLine != null) {
            goodsReceiptLines.add(goodsReceiptLine);
            goodsReceiptLine.setGoodsReceipt(this);
        }
    }
}
