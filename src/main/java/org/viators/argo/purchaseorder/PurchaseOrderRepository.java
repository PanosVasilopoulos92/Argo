package org.viators.argo.purchaseorder;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderT, Long> {

    @EntityGraph(attributePaths = {"supplier", "requisition", "poLines"})
    Optional<PurchaseOrderT> findByPublicId(String publicId);

}
