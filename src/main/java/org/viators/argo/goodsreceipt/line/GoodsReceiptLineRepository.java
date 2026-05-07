package org.viators.argo.goodsreceipt.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsReceiptLineRepository extends JpaRepository<GoodsReceiptLineT, Long> {
}
