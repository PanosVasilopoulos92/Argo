package org.viators.argo.purchaseorder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderT, Long> {

    Optional<PurchaseOrderT> findByPublicId(String publicId);

}
