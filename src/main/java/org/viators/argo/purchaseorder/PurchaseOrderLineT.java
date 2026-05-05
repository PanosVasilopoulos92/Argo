package org.viators.argo.purchaseorder;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;
import org.viators.argo.quotation.QuotationT;
import org.viators.argo.requisition.RequisitionLineT;
import org.viators.argo.supplier.SupplierT;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_lines")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseOrderLineT extends BaseEntity {

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "line_total")
    private BigDecimal lineTotal;

    @Column(name = "snapshot_item_code", nullable = false, updatable = false)
    private String snapShotItemCode;

    @Column(name = "snapshot_item_name", nullable = false, updatable = false)
    private String snapShotItemName;

    @Column(name = "snapshot_item_description", nullable = false, updatable = false)
    private String snapshotItemDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "snapshot_category", nullable = false, updatable = false)
    private ItemCategoryEnum snapshotItemCategory;

    @Column(name = "snapshot_unit_of_measurement", nullable = false, updatable = false)
    private UnitOfMeasurementEnum snapshotUnitOfMeasurement;

    @Column(name = "snapshot_part_number", updatable = false)
    private String snapshotPartNumber;

    @Column(name = "snapshot_manufacturer", updatable = false)
    private String snapshotManufacturer;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quotation_id", referencedColumnName = "id", unique = true)
    private QuotationT quotation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", referencedColumnName = "id", nullable = false)
    private PurchaseOrderT purchaseOrder;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requisition_line_id", referencedColumnName = "id", nullable = false)
    private RequisitionLineT requisitionLine;
}
