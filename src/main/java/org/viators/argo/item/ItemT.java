package org.viators.argo.item;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemT extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 300)
    private String description;

    @Column(name = "item_code", nullable = false, updatable = false, unique = true)
    private String itemCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, updatable = false)
    private ItemCategoryEnum itemCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measurement", nullable = false)
    private UnitOfMeasurementEnum unitOfMeasurement;

    @Column(name = "part_number")
    private String partNumber;

    @Column(name = "supplier")
    private String supplier;
}
