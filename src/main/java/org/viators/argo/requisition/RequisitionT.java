package org.viators.argo.requisition;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.person.PersonT;
import org.viators.argo.requisition.enums.RequisitionPriorityEnum;
import org.viators.argo.requisition.enums.RequisitionStateEnum;
import org.viators.argo.requisition.enums.RequisitionTypeEnum;
import org.viators.argo.requisition.line.RequisitionLineT;
import org.viators.argo.vessel.VesselT;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "requisitions")
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RequisitionT extends BaseEntity {

    @Column(name = "requisition_number", nullable = false, updatable = false, unique = true)
    private String requisitionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "requisition_type", nullable = false, updatable = false)
    private RequisitionTypeEnum requisitionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @Builder.Default
    private RequisitionStateEnum requisitionState = RequisitionStateEnum.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @Builder.Default
    private RequisitionPriorityEnum requisitionPriority = RequisitionPriorityEnum.NORMAL;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "required_by_date")
    private LocalDate requiredByDate;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "submitted_by")
    private String submittedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approval_remarks")
    private String approvalRemarks;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Column(name = "rejected_by")
    private String rejectedBy;

    @Column(name = "rejected_reason")
    private String rejectedReason;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vessel_id", updatable = false)
    private VesselT targetVessel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", updatable = false, nullable = false)
    private PersonT raisedBy;

    @OneToMany(mappedBy = "requisition", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RequisitionLineT> lines = new HashSet<>();

    @OneToMany(mappedBy = "requisition", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RequisitionApprovalHistoryT> approvalHistory = new HashSet<>();

    @Formula("select count(rl.id) from requisition_lines rl where rl.requisition_id = id")
    @Setter(AccessLevel.NONE)
    private Integer numberOfLines;

    public void addReqLine(RequisitionLineT reqLine) {
        lines.add(reqLine);
        reqLine.setRequisition(this);
    }

    public void addReqApprovalHistoryEntry(RequisitionApprovalHistoryT approval) {
        approvalHistory.add(approval);
        approval.setRequisition(this);
    }

}
