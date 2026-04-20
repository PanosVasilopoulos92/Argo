package org.viators.argo.common.sequence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.viators.argo.item.enums.ItemCategoryEnum;

@Entity
@Table(name = "category_sequences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCategorySequenceT {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, updatable = false)
    private ItemCategoryEnum category;

    @Column(name = "last_val", nullable = false)
    private long lastVal;
}
