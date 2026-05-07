package org.viators.argo.goodsreceipt.sequence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoodsReceiptSequenceRepository extends JpaRepository<GoodsReceiptSequenceT, Long> {

    Optional<GoodsReceiptSequenceT> findFirstByYearOrderByLastValue(Integer year);
}
