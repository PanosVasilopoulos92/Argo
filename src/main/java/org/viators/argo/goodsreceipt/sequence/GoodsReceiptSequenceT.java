package org.viators.argo.goodsreceipt.sequence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "goods_receipt_sequence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoodsReceiptSequenceT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year", nullable = false, updatable = false)
    private Integer year;

    @Column(name = "last_value", nullable = false, updatable = false)
    private Long lastValue;

    @Column(name = "final_formatted_value", nullable = false, updatable = false)
    private String finalFormattedValue;

    public GoodsReceiptSequenceT(Integer year, Long lastValue, String finalFormattedValue) {
        this.year = year;
        this.lastValue = lastValue;
        this.finalFormattedValue = finalFormattedValue;
    }
}
