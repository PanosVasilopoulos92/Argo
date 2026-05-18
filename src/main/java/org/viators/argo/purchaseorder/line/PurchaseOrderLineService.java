package org.viators.argo.purchaseorder.line;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.goodsreceipt.line.GoodsReceiptLineT;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderLineService {

    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    @Transactional(readOnly = true)
    public List<PurchaseOrderLineT> getPOLines(Set<String> poLineIds) {
        return purchaseOrderLineRepository.findAllByPublicIdIn(poLineIds);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderLineT> getPOLinesForProvidedPOs(List<Long> poIds) {
        return purchaseOrderLineRepository.findAllPOLinesForPOs(poIds);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderLineT findByPublicId(String poLinePublicId) {
        return purchaseOrderLineRepository.findByPublicId(poLinePublicId)
            .orElseThrow(() -> new ResourceNotFoundException("PO line", "publicId", poLinePublicId));
    }
}
