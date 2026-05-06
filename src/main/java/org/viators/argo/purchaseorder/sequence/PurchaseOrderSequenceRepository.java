package org.viators.argo.purchaseorder.sequence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderSequenceRepository extends JpaRepository<PurchaseOrderSequenceT, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PurchaseOrderSequenceT> findFirstByYearOrderByLastValueDesc(Integer year);
}
