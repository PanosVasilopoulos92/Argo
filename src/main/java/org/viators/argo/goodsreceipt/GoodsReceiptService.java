package org.viators.argo.goodsreceipt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.goodsreceipt.dto.request.CreateGoodsReceiptRequest;
import org.viators.argo.goodsreceipt.dto.request.GoodsReceiptLinesRequest;
import org.viators.argo.goodsreceipt.dto.response.GoodsReceiptDetailsResponse;
import org.viators.argo.goodsreceipt.dto.response.GoodsReceiptLineDetailsResponse;
import org.viators.argo.goodsreceipt.dto.response.GoodsReceiptLineSummaryResponse;
import org.viators.argo.goodsreceipt.enums.ReceiptLineFlagEnum;
import org.viators.argo.goodsreceipt.line.GoodsReceiptLineRepository;
import org.viators.argo.goodsreceipt.line.GoodsReceiptLineT;
import org.viators.argo.goodsreceipt.sequence.GoodsReceiptSequenceRepository;
import org.viators.argo.goodsreceipt.sequence.GoodsReceiptSequenceT;
import org.viators.argo.purchaseorder.PurchaseOrderService;
import org.viators.argo.purchaseorder.PurchaseOrderT;
import org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum;
import org.viators.argo.purchaseorder.line.PurchaseOrderLineService;
import org.viators.argo.purchaseorder.line.PurchaseOrderLineT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoodsReceiptService {

    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderLineService purchaseOrderLineService;
    private final GoodsReceiptSequenceRepository goodsReceiptSequenceRepository;
    private final GoodsReceiptLineRepository goodsReceiptLineRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;

    @Transactional
    public GoodsReceiptDetailsResponse create(CreateGoodsReceiptRequest request) {
        GoodsReceiptT goodsReceipt = new GoodsReceiptT();
        List<GoodsReceiptLinesRequest> receiptLinesRequest = request.receiptLines();
        List<PurchaseOrderLineT> poLines = validateCreateRequestAndLoadPoLines(request);
        PurchaseOrderT purchaseOrder = poLines.getFirst().getPurchaseOrder();

        goodsReceipt.setGoodsReceiptNumber(generateSequence());
        goodsReceipt.setReceiptDate(request.receiptDate());
        goodsReceipt.setDeliveryNotes(request.deliveryNotes());
        goodsReceipt.setPurchaseOrder(purchaseOrder);

        poLines.forEach(
            poLine -> {
                GoodsReceiptLineT goodsReceiptLine = new GoodsReceiptLineT();

                GoodsReceiptLinesRequest receiptLineRequest = receiptLinesRequest.stream()
                    .filter(e -> Objects.equals(e.poLinePublicId(), poLine.getPublicId()))
                    .findFirst()
                    .orElseThrow();

                BigDecimal totalQuantityReceivedForPOLine = computeTotalQuantityReceivedForPOLines(
                    poLine.getPublicId(), receiptLineRequest.receivedQuantity());

                goodsReceiptLine.setReceivedQuantity(totalQuantityReceivedForPOLine);
                goodsReceiptLine.setReceivedGoodsCondition(receiptLineRequest.receivedGoodsCondition());
                goodsReceiptLine.setReceiptLineFlag(calculateReceiptLineFlagEnum(poLine, totalQuantityReceivedForPOLine));
                goodsReceiptLine.setNotes(receiptLineRequest.notes());
                goodsReceiptLine.setPoLine(poLine);

                goodsReceipt.addReceiptLine(goodsReceiptLine);
            }
        );

        GoodsReceiptT savedGoodsReceipt = goodsReceiptRepository.save(goodsReceipt);

        List<GoodsReceiptLineSummaryResponse> receiptLinesCreated = goodsReceipt.getGoodsReceiptLines().stream()
            .map(GoodsReceiptLineSummaryResponse::from)
            .toList();

        return GoodsReceiptDetailsResponse.from(savedGoodsReceipt, receiptLinesCreated);
    }

    // Private helper methods
    private String generateSequence() {
        int currentYear = LocalDate.now().getYear();
        GoodsReceiptSequenceT latestSeq = goodsReceiptSequenceRepository.findFirstByYearOrderByLastValueDesc(currentYear)
            .orElse(new GoodsReceiptSequenceT(currentYear, 0L, null));

        GoodsReceiptSequenceT nextVal = new GoodsReceiptSequenceT(
            currentYear, latestSeq.getLastValue() + 1L, null
        );

        String finalValueFormatted = "GR-" + currentYear +
            String.format("%06d", nextVal.getLastValue());

        nextVal.setFinalFormattedValue(finalValueFormatted);
        goodsReceiptSequenceRepository.save(nextVal);

        return finalValueFormatted;
    }

    private List<PurchaseOrderLineT> validateCreateRequestAndLoadPoLines(CreateGoodsReceiptRequest request) {
        PurchaseOrderT purchaseOrder = purchaseOrderService.getActivePO(request.poPublicId());

        if (purchaseOrder.getPurchaseOrderState() != PurchaseOrderStateEnum.ACKNOWLEDGED &&
            purchaseOrder.getPurchaseOrderState() != PurchaseOrderStateEnum.PARTIALLY_RECEIVED) {
            throw new BusinessValidationException("Only for POs in state 'ACKNOWLEDGED' and 'PARTIALLY_RECEIVED' can a receipt be created");
        }

        Set<String> poLinePublicIds = request.receiptLines().stream()
            .map(GoodsReceiptLinesRequest::poLinePublicId)
            .collect(Collectors.toSet());

        validateUniquePOLineIdsProvided(request.receiptLines(), poLinePublicIds);

        List<PurchaseOrderLineT> poLines = purchaseOrderLineService.getPOLines(poLinePublicIds);

        Set<String> poLinesNotBelongingToPO = poLines.stream()
            .filter(poLine -> !Objects.equals(poLine.getPurchaseOrder().getId(), purchaseOrder.getId()))
            .map(PurchaseOrderLineT::getPublicId)
            .collect(Collectors.toSet());

        if (!poLinesNotBelongingToPO.isEmpty()) {
            throw new BusinessValidationException("The following PO lines do not correspond to PO that the receipt mentions: %s"
                .formatted(poLinesNotBelongingToPO));
        }

        return poLines;
    }

    private void validateUniquePOLineIdsProvided(List<GoodsReceiptLinesRequest> receiptLineIdsProvided, Set<String> poLinePublicIds) {
        List<String> group = receiptLineIdsProvided.stream()
            .map(GoodsReceiptLinesRequest::poLinePublicId)
            .collect(Collectors.toCollection(ArrayList::new));

        group.removeAll(poLinePublicIds);

        if (!group.isEmpty()) {
            throw new InvalidStateException("You did not provide unique po lines. Please check and try again");
        }
    }

    private BigDecimal computeTotalQuantityReceivedForPOLines(String  poLinePublicId, BigDecimal quantityReceivedNow) {
        Set<GoodsReceiptLineT> receiptLines = goodsReceiptLineRepository.findAllNonCancelledForPOLine(poLinePublicId);

        return receiptLines.stream()
            .filter(Objects::nonNull)
            .map(GoodsReceiptLineT::getReceivedQuantity)
            .reduce(quantityReceivedNow, BigDecimal::add);
    }

    private ReceiptLineFlagEnum calculateReceiptLineFlagEnum(PurchaseOrderLineT poLine, BigDecimal totalQuantityReceived) {

        if (totalQuantityReceived.compareTo(poLine.getQuantity()) > 0) {
            return ReceiptLineFlagEnum.OVER_RECEIVED;
        } else if (totalQuantityReceived.compareTo(poLine.getQuantity()) < 0) {
            return ReceiptLineFlagEnum.UNDER_RECEIVED;
        } else {
            return ReceiptLineFlagEnum.WELL_RECEIVED;
        }
    }

}
