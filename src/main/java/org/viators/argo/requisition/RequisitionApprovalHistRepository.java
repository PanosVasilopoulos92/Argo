package org.viators.argo.requisition;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequisitionApprovalHistRepository extends JpaRepository<RequisitionApprovalHistoryT, Long> {

    @EntityGraph(attributePaths = {"requisition", "approver"})
    RequisitionApprovalHistoryT findTop1ByRequisition_PublicIdOrderByCreatedAtDesc(String requisitionPublicId);
}
