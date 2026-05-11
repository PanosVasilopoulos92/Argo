package org.viators.argo.goodsreceipt.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GoodsReceiptLineRepository extends JpaRepository<GoodsReceiptLineT, Long> {

    @Query("""
           select grl from GoodsReceiptLineT grl
           join grl.goodsReceipt gr
           join grl.poLine po
           where po.publicId = :poLinePublicId
           and gr.cancelledAt is null
           """)
    Set<GoodsReceiptLineT> findAllNonCancelledForPOLine(@Param("poLinePublicId") String poLinePublicId);
}
