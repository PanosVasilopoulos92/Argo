package org.viators.argo.requisition;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.item.ItemT;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;
import org.viators.argo.quotation.QuotationT;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "requisition_lines")
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RequisitionLineT extends BaseEntity {

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measurement", nullable = false)
    private UnitOfMeasurementEnum unitOfMeasurementEnum;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "snapshot_item_code", nullable = false, updatable = false)
    private String snapShotItemCode;

    @Column(name = "snapshot_item_name", nullable = false, updatable = false)
    private String snapShotItemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "snapshot_category", nullable = false, updatable = false)
    private ItemCategoryEnum snapshotItemCategory;

    @Column(name = "snapshot_manufacturer", updatable = false)
    private String snapshotManufacturer;

    @Column(name = "snapshot_unit_of_measurement", nullable = false, updatable = false)
    private UnitOfMeasurementEnum snapshotUnitOfMeasurement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false, updatable = false)
    private ItemT catalogItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id", referencedColumnName = "id", nullable = false, updatable = false)
    private RequisitionT requisition;

    @OneToMany(mappedBy = "reqLine", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuotationT> quotations = new HashSet<>();

}
