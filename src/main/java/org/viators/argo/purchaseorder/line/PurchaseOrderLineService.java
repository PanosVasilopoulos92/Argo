package org.viators.argo.purchaseorder.line;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderLineService {

    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    @Transactional(readOnly = true)
    public List<PurchaseOrderLineT> getPOLines(Set<String> poLineIds) {
        return purchaseOrderLineRepository.findAllByPublicIDsIn(poLineIds);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderLineT> getPOLinesForProvidedPOs(List<Long> poIds) {
        return purchaseOrderLineRepository.findAllPOLinesForPOs(poIds);
    }
}
