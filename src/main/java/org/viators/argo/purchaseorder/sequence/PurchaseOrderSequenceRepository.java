package org.viators.argo.purchaseorder.sequence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderSequenceRepository extends JpaRepository<PurchaseOrderSequenceT, Long> {

    Optional<PurchaseOrderSequenceT> findFirstByYearOrderByLastValueDesc(Integer year);
}
