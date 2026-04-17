package org.viators.argo.assignment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.person.seafarer.SeafarerT;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;
import org.viators.argo.vessel.VesselT;

import java.time.LocalDate;

/*
    Dedicated Entity with a Surrogate Key
 */
@Entity
@Table(name = "assignments")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AssignmentT extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_rank", nullable = false)
    private SeafarerRankEnum assignmentRank;

    @Column(name = "sign_on_date", nullable = false)
    private LocalDate signOnDate;

    @Column(name = "expected_sign_off_date")
    private LocalDate expectedSignOffDate;

    @Column(name = "actual_signed_off_date")
    private LocalDate actualSignedOffDate;

    @Column(name = "sign_on_port", nullable = false, length = 100)
    private String signOnPort;

    @Column(name = "sign_off_port", length = 100)
    private String signOffPort;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "sign_off_remarks", length = 500)
    private String signOffRemarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_state", nullable = false)
    @Builder.Default
    private AssignmentStateEnum assignmentState = AssignmentStateEnum.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vessel_id", nullable = false)
    private VesselT vessel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seafarer_id", nullable = false)
    private SeafarerT seafarer;
}
