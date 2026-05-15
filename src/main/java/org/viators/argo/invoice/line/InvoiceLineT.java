package org.viators.argo.invoice.line;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.invoice.InvoiceT;
import org.viators.argo.invoice.enums.MatchStatusEnum;
import org.viators.argo.purchaseorder.line.PurchaseOrderLineT;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_lines")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvoiceLineT extends BaseEntity {

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "line_total", nullable = false)
    private BigDecimal lineTotal;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_status")
    @Builder.Default
    private MatchStatusEnum matchStatus = MatchStatusEnum.UNMATCHED;

    @Column(name = "price_variance")
    private BigDecimal priceVariance;

    @Column(name = "quantity_variance")
    private BigDecimal quantityVariance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", referencedColumnName = "id", nullable = false)
    private InvoiceT invoice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "po_line_id", referencedColumnName = "id", nullable = false)
    private PurchaseOrderLineT poLine;
}
