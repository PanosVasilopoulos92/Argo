package org.viators.argo.goodsreceipt;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.goodsreceipt.dto.request.CancelGoodsReceiptRequest;
import org.viators.argo.goodsreceipt.dto.request.CreateGoodsReceiptRequest;
import org.viators.argo.goodsreceipt.dto.request.GoodsReceiptLinesRequest;
import org.viators.argo.goodsreceipt.dto.request.SearchReceiptFilterRequest;
import org.viators.argo.goodsreceipt.dto.response.GoodsReceiptDetailsResponse;
import org.viators.argo.goodsreceipt.dto.response.GoodsReceiptLineSummaryResponse;
import org.viators.argo.goodsreceipt.dto.response.GoodsReceiptSummaryResponse;
import org.viators.argo.goodsreceipt.enums.GoodsReceiptStateEnum;
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
import org.viators.argo.requisition.RequisitionService;
import org.viators.argo.requisition.RequisitionT;
import org.viators.argo.requisition.enums.RequisitionStateEnum;
import org.viators.argo.requisition.line.RequisitionLineT;
import org.viators.argo.user.UserService;

import java.math.BigDecimal;
import java.time.Instant;
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
    private final RequisitionService requisitionService;
    private final UserService userService;

    @Transactional
    public GoodsReceiptDetailsResponse create(CreateGoodsReceiptRequest request) {
        GoodsReceiptT goodsReceipt = new GoodsReceiptT();
        List<GoodsReceiptLinesRequest> receiptLinesRequest = request.receiptLines();
        List<PurchaseOrderLineT> poLines = validateCreateRequestAndLoadPoLines(request);
        Long purchaseOrderId = poLines.getFirst().getPurchaseOrder().getId();
        PurchaseOrderT purchaseOrder = purchaseOrderService.getActivePOByDatabaseId(purchaseOrderId);

        goodsReceipt.setGoodsReceiptNumber(generateSequence());
        goodsReceipt.setReceiptDate(request.receiptDate());
        goodsReceipt.setDeliveryNotes(request.deliveryNotes());
        goodsReceipt.setPurchaseOrder(purchaseOrder);

        List<String> poLinesWithPartialReceivedQuantities = new ArrayList<>();
        List<RequisitionLineT> requisitionsWithPartialReceivedQuantities = new ArrayList<>();

        poLines.forEach(
            poLine -> {
                GoodsReceiptLineT goodsReceiptLine = new GoodsReceiptLineT();
                RequisitionLineT requisitionLine = poLine.getRequisitionLine();

                GoodsReceiptLinesRequest receiptLineRequest = receiptLinesRequest.stream()
                    .filter(e -> Objects.equals(e.poLinePublicId(), poLine.getPublicId()))
                    .findFirst()
                    .orElseThrow();

                BigDecimal totalQuantityReceivedForPOLine = computeTotalQuantityReceivedForPOLines(
                    poLine.getPublicId(), receiptLineRequest.receivedQuantity());

                goodsReceiptLine.setReceivedQuantity(receiptLineRequest.receivedQuantity());
                goodsReceiptLine.setReceivedGoodsCondition(receiptLineRequest.receivedGoodsCondition());
                goodsReceiptLine.setReceiptLineFlag(calculateReceiptLineFlagEnum(poLine, totalQuantityReceivedForPOLine));
                goodsReceiptLine.setNotes(receiptLineRequest.notes());
                goodsReceiptLine.setPoLine(poLine);

                goodsReceipt.addReceiptLine(goodsReceiptLine);

                if (goodsReceiptLine.getReceiptLineFlag() == ReceiptLineFlagEnum.UNDER_RECEIVED) {
                    poLinesWithPartialReceivedQuantities.add(poLine.getPublicId());
                    requisitionsWithPartialReceivedQuantities.add(requisitionLine);
                }
            }
        );

        Long parentRequisitionId = requisitionsWithPartialReceivedQuantities.getFirst().getRequisition().getId();
        RequisitionT parentRequisition = requisitionService.getActiveRequisitionByDatabaseId(parentRequisitionId);

        List<PurchaseOrderT> posRelatedToParentRequisition = purchaseOrderService.getAllPOsForRequisition(parentRequisition.getPublicId());
        List<Long> posRelatedToParentRequisitionIds = posRelatedToParentRequisition.stream()
            .map(PurchaseOrderT::getId)
            .toList();
        List<PurchaseOrderLineT> poLineRelatedToParentRequisition = purchaseOrderLineService
            .getPOLinesForProvidedPOs(posRelatedToParentRequisitionIds);


        if (posRelatedToParentRequisition.size() == 1) {
            if (!poLinesWithPartialReceivedQuantities.isEmpty()) {
                purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.PARTIALLY_RECEIVED);
            } else {
                purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.FULLY_RECEIVED);
                parentRequisition.setRequisitionState(RequisitionStateEnum.FULFILLED);
            }
        } else {
            if (!poLinesWithPartialReceivedQuantities.isEmpty()) {
                purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.PARTIALLY_RECEIVED);
            } else {
                List<String> poLinesWithUnderReceivedQuantities = validateForMultiPOs(poLineRelatedToParentRequisition);
                if (poLinesWithUnderReceivedQuantities.isEmpty()) {
                    purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.FULLY_RECEIVED);
                    parentRequisition.setRequisitionState(RequisitionStateEnum.FULFILLED);
                }
            }
        }

        GoodsReceiptT savedGoodsReceipt = goodsReceiptRepository.save(goodsReceipt);

        List<GoodsReceiptLineSummaryResponse> receiptLinesCreated = goodsReceipt.getGoodsReceiptLines().stream()
            .map(GoodsReceiptLineSummaryResponse::from)
            .toList();

        return GoodsReceiptDetailsResponse.from(savedGoodsReceipt, receiptLinesCreated);
    }

    public List<String> validateForMultiPOs(List<PurchaseOrderLineT> poLineRelatedToParentRequisition) {
        List<String> poLinesWithUnderReceivedQuantities = new ArrayList<>();
        poLineRelatedToParentRequisition.forEach(poLine -> {
                BigDecimal receivedQuantity = computeTotalQuantityReceivedForPOLines(poLine.getPublicId(), BigDecimal.ZERO);
                if (poLine.getQuantity().compareTo(receivedQuantity) < 1) {
                    poLinesWithUnderReceivedQuantities.add(poLine.getPublicId());
                }
            }
        );

        return poLinesWithUnderReceivedQuantities;
    }

    @Transactional
    public void cancelGoodsReceipt(String keycloakPublicId, String receiptPublicId, CancelGoodsReceiptRequest request) {
        String loggedInUser = userService.getUser(keycloakPublicId).getUsername();
        GoodsReceiptT goodsReceipt = loadReceiptAndValidateVersion(receiptPublicId, request.version());
        PurchaseOrderT purchaseOrder = goodsReceipt.getPurchaseOrder();

        if (goodsReceipt.getReceiptState() != GoodsReceiptStateEnum.RECORDED) {
            throw new InvalidStateException("Only receipts in state 'RECORDED' can be cancelled");
        }

        handlePOStateChange(purchaseOrder);

        goodsReceipt.getGoodsReceiptLines().forEach(
            line -> line.setStatus(ResourceStatusEnum.INACTIVE)
        );

        goodsReceipt.setCancelledAt(Instant.now());
        goodsReceipt.setCancelledBy(loggedInUser);
        goodsReceipt.setCancellationReason(request.cancellationReason());

    }

    // Read only methods
    @Transactional(readOnly = true)
    public GoodsReceiptDetailsResponse getGoodsReceipt(String receiptPublicId) {
        GoodsReceiptT goodsReceipt = goodsReceiptRepository.findByPublicId(receiptPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Receipt", "publicId", receiptPublicId));

        List<GoodsReceiptLineSummaryResponse> receiptLinesSummary = goodsReceipt.getGoodsReceiptLines().stream()
            .map(GoodsReceiptLineSummaryResponse::from)
            .toList();

        return GoodsReceiptDetailsResponse.from(goodsReceipt, receiptLinesSummary);
    }

    @Transactional(readOnly = true)
    public Page<GoodsReceiptSummaryResponse> getGoodsReceiptsFiltered(SearchReceiptFilterRequest filter,
                                                                      Pageable pageable) {

        Specification<GoodsReceiptT> specs = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(filter.goodsReceiptNumber())) {
            specs = specs.and(GoodsReceiptSpecs.hasReceiptNumber(filter.goodsReceiptNumber()));
        }

        if (StringUtils.hasText(filter.poPublicId())) {
            specs = specs.and(GoodsReceiptSpecs.hasPOPublicId(filter.poPublicId()));
        }

        if (StringUtils.hasText(filter.poNumber())) {
            specs = specs.and(GoodsReceiptSpecs.hasPONumber(filter.poNumber()));
        }

        if (StringUtils.hasText(filter.supplierPublicId())) {
            specs = specs.and(GoodsReceiptSpecs.hasSupplierPublicId(filter.supplierPublicId()));
        }

        if (filter.receiptState() != null) {
            specs = specs.and(GoodsReceiptSpecs.hasReceiptState(filter.receiptState()));
        }

        if (filter.containsOverReceivedLine()) {
            specs = specs.and(GoodsReceiptSpecs.hasOverReceived());
        }

        if (filter.containsDamagedOrWrongItem()) {
            specs = specs.and(GoodsReceiptSpecs.hasDamagedOrWrongItem());
        }

        specs = specs.and(GoodsReceiptSpecs.hasReceiptDateRange(filter.receiptDateFrom(), filter.receiptDateTo()));

        return goodsReceiptRepository.findAll(specs, pageable)
            .map(GoodsReceiptSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<GoodsReceiptSummaryResponse> getReceiptsForPO(String poPublicId, Pageable pageable) {
        Page<GoodsReceiptT> receipts = goodsReceiptRepository.findByPurchaseOrder_PublicId(poPublicId, pageable);

        return receipts.map(GoodsReceiptSummaryResponse::from);

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

    private BigDecimal computeTotalQuantityReceivedForPOLines(String poLinePublicId, BigDecimal quantityReceivedNow) {
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

    private GoodsReceiptT loadReceiptAndValidateVersion(String receiptPublicId, Long providedVersion) {
        GoodsReceiptT goodsReceipt = goodsReceiptRepository.findByPublicId(receiptPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Receipt", "publicId", receiptPublicId));

        if (!Objects.equals(goodsReceipt.getVersion(), providedVersion)) {
            throw new OptimisticLockException("Another user has modified this resource concurrently. Please try again");
        }

        return goodsReceipt;
    }

    private void handlePOStateChange(PurchaseOrderT purchaseOrder) {
        List<GoodsReceiptT> purchaseOrderReceipts = goodsReceiptRepository.findAllByPurchaseOrder_Id(purchaseOrder.getId())
            .stream()
            .filter(goodsReceiptT -> goodsReceiptT.getReceiptState() != GoodsReceiptStateEnum.CANCELLED)
            .toList();

        if (purchaseOrderReceipts.size() == 1) {
            if (purchaseOrder.getPurchaseOrderState() == PurchaseOrderStateEnum.FULLY_RECEIVED) {
                purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.PARTIALLY_RECEIVED);
            }
            if (purchaseOrder.getPurchaseOrderState() == PurchaseOrderStateEnum.PARTIALLY_RECEIVED) {
                purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.ACKNOWLEDGED);
            }
        } else {
            purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.PARTIALLY_RECEIVED);
        }
    }

}
