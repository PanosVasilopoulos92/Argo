package org.viators.argo.goodsreceipt.line;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoodsReceiptLineService {

    private final GoodsReceiptLineRepository goodsReceiptLineRepository;

    public Set<GoodsReceiptLineT> getReceiptLinesForPOLine(String poLinePublicId) {
        return goodsReceiptLineRepository.findAllNonCancelledForPOLine(poLinePublicId);
    }

}
