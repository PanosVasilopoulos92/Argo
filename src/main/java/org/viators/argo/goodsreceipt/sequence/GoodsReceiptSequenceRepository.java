package org.viators.argo.goodsreceipt.sequence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoodsReceiptSequenceRepository extends JpaRepository<GoodsReceiptSequenceT, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<GoodsReceiptSequenceT> findFirstByYearOrderByLastValue(Integer year);
}
