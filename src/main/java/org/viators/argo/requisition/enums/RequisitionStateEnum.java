package org.viators.argo.requisition.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequisitionStateEnum {
    DRAFT("Requisition created but not yet visible to approvers. Editable by creator."), // Initial value
    SUBMITTED("Sent into the approval queue. Lines are now immutable. Awaiting approver decision."),
    APPROVED("Started approval saga. Until final approval it can be rejected at any level of approval"),
    FINALIZED("Level 5 user approved it. Terminal. Will feed into PO creation."),
    REJECTED("Approver rejected it with a mandatory reason. Terminal."),
    CANCELLED("Creator abandoned the draft before submission. Terminal.");

    private final String description;
}
