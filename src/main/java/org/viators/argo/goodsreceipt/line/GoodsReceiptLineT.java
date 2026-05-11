package org.viators.argo.goodsreceipt.line;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.goodsreceipt.GoodsReceiptT;
import org.viators.argo.goodsreceipt.enums.ReceiptLineFlagEnum;
import org.viators.argo.goodsreceipt.enums.ReceivedGoodsConditionEnum;
import org.viators.argo.purchaseorder.line.PurchaseOrderLineT;

import java.math.BigDecimal;

@Entity
@Table(name = "goods_receipt_lines")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GoodsReceiptLineT extends BaseEntity {

    @Column(name = "received_quantity", nullable = false)
    private BigDecimal receivedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "received_goods_condition", nullable = false)
    @Builder.Default
    private ReceivedGoodsConditionEnum receivedGoodsCondition = ReceivedGoodsConditionEnum.OK;

    @Enumerated(EnumType.STRING)
    @Column(name = "line_flag", nullable = false)
    private ReceiptLineFlagEnum receiptLineFlag;

    @Column(name = "notes", length = 400)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", referencedColumnName = "id", nullable = false, updatable = false)
    private GoodsReceiptT goodsReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_line_id", referencedColumnName = "id", nullable = false, updatable = false)
    private PurchaseOrderLineT poLine;
}
