package org.viators.argo.requisition;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.person.PersonT;
import org.viators.argo.user.UserLevelEnum;

@Entity
@Table(name = "requisition_approvals_history")
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RequisitionApprovalHistoryT extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id", referencedColumnName = "id", nullable = false, updatable = false)
    private RequisitionT requisition;

    @Column(name = "approver_username", nullable = false, updatable = false)
    private String approverUsername;

    @Column(name = "approver_level_at_action", nullable = false, updatable = false)
    private UserLevelEnum approverLevelAtAction;

    @Column(name = "remarks", length = 400)
    private String remarks;
}
