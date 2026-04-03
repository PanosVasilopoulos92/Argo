package org.viators.argo.person.seafarer;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.person.PersonT;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

import java.time.LocalDate;

@Entity
@Table(name = "seafarers")
@DiscriminatorValue(value = "SEAFARER")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SeafarerT extends PersonT {

    @Enumerated(EnumType.STRING)
    @Column(name = "rank", nullable = false)
    private SeafarerRankEnum rank;

    @Column(name = "sb_number", unique = true)
    private String seamanBookNumber;

    @Column(name = "sb_issued")
    private LocalDate sbIssued;

    @Column(name = "sb_expiry")
    private LocalDate sbExpiry;
}
