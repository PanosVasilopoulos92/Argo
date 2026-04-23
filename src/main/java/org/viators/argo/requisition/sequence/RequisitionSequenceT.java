package org.viators.argo.requisition.sequence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "requisition_sequences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionSequenceT {

    @Id
    @Column(name = "year", nullable = false, updatable = false)
    private Integer year;

    @Column(name = "last_value", nullable = false, updatable = false)
    private Long lastValue;

    @Column(name = "final_formated_value", nullable = false, updatable = false)
    private String finalFormattedValue;
}
