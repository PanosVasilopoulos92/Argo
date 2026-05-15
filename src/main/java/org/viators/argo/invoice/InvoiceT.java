package org.viators.argo.invoice;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.invoice.enums.InvoiceStateEnum;
import org.viators.argo.invoice.enums.PaymentMethodEnum;
import org.viators.argo.invoice.line.InvoiceLineT;
import org.viators.argo.purchaseorder.PurchaseOrderT;
import org.viators.argo.supplier.SupplierT;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvoiceT extends BaseEntity {

    @Column(name = "invoice_number", nullable = false, updatable = false)
    private String invoiceNumber;

    @Column(name = "supplier_inv_ref")
    private String supplierInvoiceReference;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate invoiceDueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyEnum currency;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @Builder.Default
    private InvoiceStateEnum invoiceState = InvoiceStateEnum.RECEIVED;

    @Column(name = "matched_at")
    private Instant matchedAt; //populated on transition to MATCHED or DISPUTED

    @Column(name = "matched_by")
    private String matchedBy;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @Column(name = "cancellation_reason", length = 400)
    private String cancellationReason;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Column(name = "rejected_by")
    private String rejectedBy;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "paid_by")
    private String paidBy;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethodEnum paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id", nullable = false)
    private SupplierT supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", referencedColumnName = "id")
    private PurchaseOrderT purchaseOrder;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<InvoiceLineT> invoiceLines = new HashSet<>();

    public void addInvoiceLine(InvoiceLineT invoiceLine) {
        if (invoiceLine != null) {
            invoiceLines.add(invoiceLine);
            invoiceLine.setInvoice(this);
        }
    }
}
