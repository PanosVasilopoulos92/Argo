package org.viators.argo.purchaseorder;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.item.ItemT;
import org.viators.argo.purchaseorder.dto.request.*;
import org.viators.argo.purchaseorder.dto.response.PODetailsResponse;
import org.viators.argo.purchaseorder.dto.response.POLineSummaryResponse;
import org.viators.argo.purchaseorder.dto.response.POSummaryResponse;
import org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum;
import org.viators.argo.purchaseorder.enums.PurchaseOrderTypeEnum;
import org.viators.argo.purchaseorder.sequence.PurchaseOrderSequenceRepository;
import org.viators.argo.purchaseorder.sequence.PurchaseOrderSequenceT;
import org.viators.argo.quotation.QuotationService;
import org.viators.argo.quotation.QuotationT;
import org.viators.argo.quotation.enums.QuotationStateEnum;
import org.viators.argo.requisition.RequisitionLineService;
import org.viators.argo.requisition.RequisitionLineT;
import org.viators.argo.requisition.RequisitionT;
import org.viators.argo.supplier.SupplierT;
import org.viators.argo.user.UserService;
import org.viators.argo.user.UserT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderService {

    private final QuotationService quotationService;
    private final RequisitionLineService requisitionLineService;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderSequenceRepository purchaseOrderSequenceRepository;
    private final UserService userService;

    private final String LOW_THRESHOLD = "500.00";
    private final String HIGH_THRESHOLD = "10000.00";

    @Transactional
    public PODetailsResponse create(CreatePORequest request) {
        Set<QuotationT> quotations = quotationService.getQuotationsForPO(request.quotationPublicIds());
        Set<String> retrievedQuotationsPublicIds = quotations.stream()
            .map(QuotationT::getPublicId)
            .collect(Collectors.toSet());

        validateAllQuotationsExist(request.quotationPublicIds(), retrievedQuotationsPublicIds);

        Map<String, RequisitionLineT> retrievedReqLines = validateQuotationsAndRetrieveReqLines(
            quotations, request.purchaseOrderType(), request.justificationNotes());

        CurrencyEnum poCurrency = fetchCurrencyAndValidateIsSame(quotations);

        SupplierT supplier = quotations.stream()
            .findFirst()
            .orElseThrow()
            .getSupplier();

        RequisitionT requisition = quotations.stream()
            .findFirst()
            .orElseThrow()
            .getReqLine()
            .getRequisition();

        PurchaseOrderT purchaseOrder = PurchaseOrderT.builder()
            .purchaseOrderNumber(generatePONumber())
            .purchaseOrderType(request.purchaseOrderType())
            .justificationNotes(request.justificationNotes())
            .currency(poCurrency)
            .supplier(supplier)
            .requisition(requisition)
            .build();

        quotations.forEach(
            q -> {
                RequisitionLineT requisitionLine = retrievedReqLines.get(q.getReqLine().getPublicId());
                ItemT catalogItem = requisitionLine.getCatalogItem();

                PurchaseOrderLineT poLine = PurchaseOrderLineT.builder()
                    .quantity(q.getQuotedQuantity())
                    .unitPrice(q.getUnitPrice())
                    .lineTotal(q.getQuotedQuantity().multiply(q.getUnitPrice())
                        .setScale(2, RoundingMode.HALF_UP)
                    )
                    .snapShotItemCode(catalogItem.getItemCode())
                    .snapShotItemName(catalogItem.getName())
                    .snapshotItemDescription(catalogItem.getDescription())
                    .snapshotItemCategory(catalogItem.getItemCategory())
                    .snapshotUnitOfMeasurement(catalogItem.getUnitOfMeasurement())
                    .snapshotPartNumber(catalogItem.getPartNumber())
                    .snapshotManufacturer(catalogItem.getManufacturer())
                    .purchaseOrder(purchaseOrder)
                    .requisitionLine(requisitionLine)
                    .build();

                poLine.addQuotation(q);
                purchaseOrder.addPOLine(poLine);
            }
        );

        BigDecimal poTotalAmount = purchaseOrder.getPoLines().stream()
            .map(PurchaseOrderLineT::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        purchaseOrder.setTotalAmount(poTotalAmount);
        purchaseOrderRepository.save(purchaseOrder);

        List<POLineSummaryResponse> poLines = purchaseOrder.getPoLines()
            .stream()
            .map(POLineSummaryResponse::from)
            .toList();

        log.info("PO with publicId {} has been created at {}. Contains {} lines and has total value of {}",
            purchaseOrder.getPublicId(), Instant.now(), poLines.size(), poTotalAmount);
        return PODetailsResponse.from(purchaseOrder, poLines);
    }

    @Transactional
    public PODetailsResponse sendPOToSupplier(String poPublicId, SendPOToSupplierRequest request) {
        PurchaseOrderT purchaseOrder = loadResourceAndValidateVersion(poPublicId, request.version());

        if (purchaseOrder.getSupplier().getStatus() == ResourceStatusEnum.INACTIVE) {
            throw new BusinessValidationException("Supplier with publicId: %s is inactive. PO cannot proceed"
                .formatted(purchaseOrder.getSupplier().getPublicId()));
        }

        if (purchaseOrder.getPurchaseOrderState() != PurchaseOrderStateEnum.DRAFT) {
            throw new InvalidStateException("Only POs in state 'DRAFT' can be sent. PO with publicId: %s is in state '%s'"
                .formatted(poPublicId, purchaseOrder.getPurchaseOrderState().name()));
        }

        purchaseOrder.setSentAt(Instant.now());
        purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.SENT);

        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        List<POLineSummaryResponse> poLines = purchaseOrder.getPoLines()
            .stream()
            .map(POLineSummaryResponse::from)
            .toList();

        return PODetailsResponse.from(purchaseOrder, poLines);
    }

    @Transactional
    public PODetailsResponse acknowledgePOFromSupplier(String keycloakId, String poPublicId, AckPOFromSupplierRequest request) {
        UserT loggedInUser = userService.getUser(keycloakId);
        PurchaseOrderT purchaseOrder = loadResourceAndValidateVersion(poPublicId, request.version());

        if (purchaseOrder.getPurchaseOrderState() != PurchaseOrderStateEnum.SENT) {
            throw new InvalidStateException("Only POs in state 'SENT' can be acknowledged by supplier. PO with publicId: %s is in state '%s'"
                .formatted(poPublicId, purchaseOrder.getPurchaseOrderState().name()));
        }

        purchaseOrder.setAcknowledgedAt(Instant.now());
        purchaseOrder.setAcknowledgedBy(loggedInUser.getUsername());
        purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.ACKNOWLEDGED);
        purchaseOrder.setSupplierAckReference(request.supplierAckReference());

        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        List<POLineSummaryResponse> poLines = purchaseOrder.getPoLines()
            .stream()
            .map(POLineSummaryResponse::from)
            .toList();

        return PODetailsResponse.from(purchaseOrder, poLines);
    }

    @Transactional
    public PODetailsResponse closePO(String keycloakId, String poPublicId, ClosePORequest request) {
        UserT loggedInUser = userService.getUser(keycloakId);
        PurchaseOrderT purchaseOrder = loadResourceAndValidateVersion(poPublicId, request.version());

        if (purchaseOrder.getPurchaseOrderState() != PurchaseOrderStateEnum.ACKNOWLEDGED) {
            throw new InvalidStateException("Only POs in state 'ACKNOWLEDGED' can be closed/finilized. PO with publicId: %s is in state '%s'"
                .formatted(poPublicId, purchaseOrder.getPurchaseOrderState().name()));
        }

        Set<String> poLinesWithoutPrice = purchaseOrder.getPoLines().stream()
            .filter(line -> line.getUnitPrice() == null)
            .map(PurchaseOrderLineT::getPublicId)
            .collect(Collectors.toSet());

        if (!poLinesWithoutPrice.isEmpty()) {
            throw new BusinessValidationException("Found Purchase line(s) that have not yet set their price: %s"
                .formatted(poLinesWithoutPrice) +
                " PO cannot close");
        }

        purchaseOrder.setClosedAt(Instant.now());
        purchaseOrder.setClosedBy(loggedInUser.getUsername());
        purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.CLOSED);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        List<POLineSummaryResponse> poLines = purchaseOrder.getPoLines()
            .stream()
            .map(POLineSummaryResponse::from)
            .toList();

        return PODetailsResponse.from(purchaseOrder, poLines);
    }

    @Transactional
    public PODetailsResponse cancelPO(String keycloakId, String poPublicId, CancelPORequest request) {
        UserT loggedInUser = userService.getUser(keycloakId);
        PurchaseOrderT purchaseOrder = loadResourceAndValidateVersion(poPublicId, request.version());

        if (purchaseOrder.getPurchaseOrderState() != PurchaseOrderStateEnum.DRAFT &&
            purchaseOrder.getPurchaseOrderState() != PurchaseOrderStateEnum.SENT) {
            throw new InvalidStateException("Only POs in state 'DRAFT' and 'SENT' can be cancelled. PO with publicId: %s is in state '%s'"
                .formatted(poPublicId, purchaseOrder.getPurchaseOrderState().name()));
        }

        Set<Long> quotationToBeReleased = purchaseOrder.getPoLines().stream()
            .map(PurchaseOrderLineT::getQuotation)
            .map(QuotationT::getId)
            .collect(Collectors.toSet());

        quotationService.releaseQuotationsFromPOLines(quotationToBeReleased);

        purchaseOrder.setCancelledAt(Instant.now());
        purchaseOrder.setCancelledBy(loggedInUser.getUsername());
        purchaseOrder.setCancellationReason(request.cancellationReason());
        purchaseOrder.setPurchaseOrderState(PurchaseOrderStateEnum.CANCELLED);

        purchaseOrderRepository.save(purchaseOrder);

        List<POLineSummaryResponse> poLines = purchaseOrder.getPoLines()
            .stream()
            .map(POLineSummaryResponse::from)
            .toList();

        return PODetailsResponse.from(purchaseOrder, poLines);
    }

    // Read only methods
    @Transactional(readOnly = true)
    public PODetailsResponse getPO(String poPublicId) {
        PurchaseOrderT purchaseOrder = purchaseOrderRepository.findByPublicId(poPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("PO", "publicId", poPublicId));

        List<POLineSummaryResponse> poLines = purchaseOrder.getPoLines().stream()
            .map(POLineSummaryResponse::from)
            .toList();

        return PODetailsResponse.from(purchaseOrder, poLines);
    }

    @Transactional(readOnly = true)
    public Page<POSummaryResponse> getPOFiltered(SearchPOFilteredRequest request, Pageable pageable) {
        Specification<PurchaseOrderT> specs = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(request.purchaseOrderNumber())) {
            specs = specs.and(POSpecs.hasPONumber(request.purchaseOrderNumber()));
        }

        if (StringUtils.hasText(request.supplierCompanyNameContaining())) {
            specs = specs.and(POSpecs.hasSupplierCompanyNameContaining(
                request.supplierCompanyNameContaining()
            ));
        }

        if (StringUtils.hasText(request.sourceRequisitionPublicId())) {
            specs = specs.and(POSpecs.hasReqPublicId(request.sourceRequisitionPublicId()));
        }

        if (request.purchaseOrderType() != null) {
            specs = specs.and(POSpecs.hasPOType(request.purchaseOrderType()));
        }

        if (request.purchaseOrderState() != null) {
            specs = specs.and(POSpecs.hasPOState(request.purchaseOrderState()));
        }

        if (request.currency() != null) {
            specs = specs.and(POSpecs.hasCurrency(request.currency()));
        }

        specs = specs.and(POSpecs.hasSentDateRange(request.sentAtFrom(), request.sentAtTo()));
        specs = specs.and(POSpecs.hasTotalAmountRange(request.totalAmountMin(), request.totalAmountMax()));

        return purchaseOrderRepository.findAll(specs, pageable)
            .map(POSummaryResponse::from);
    }

    // Private helper methods
    private String generatePONumber() {
        int currentYear = LocalDate.now().getYear();
        PurchaseOrderSequenceT latestPurchaseOrderSequence = purchaseOrderSequenceRepository.findFirstByYearOrderByLastValueDesc(currentYear)
            .orElse(new PurchaseOrderSequenceT(currentYear, 0L, null));

        PurchaseOrderSequenceT nextVal = new PurchaseOrderSequenceT(
            currentYear, latestPurchaseOrderSequence.getLastValue() + 1L, null
        );

        String finalFormattedValue = "PO-".concat(String.valueOf(currentYear)).concat("-")
            .concat(String.format("%06d", nextVal.getLastValue()));

        nextVal.setFinalFormattedValue(finalFormattedValue);
        nextVal = purchaseOrderSequenceRepository.save(nextVal);

        return nextVal.getFinalFormattedValue();
    }

    private Map<String, RequisitionLineT> validateQuotationsAndRetrieveReqLines(Set<QuotationT> quotations,
                                                                                PurchaseOrderTypeEnum poType,
                                                                                String justificationNotes) {

        validateNumberOfQuotationsAgainstAmountAndPOType(quotations, poType, justificationNotes);

        // All quotations have validUntil ≥ today
        List<String> expiredQuotations = quotations.stream()
            .filter(q -> q.getValidUntil().isBefore(LocalDate.now()))
            .map(QuotationT::getPublicId)
            .toList();

        if (!expiredQuotations.isEmpty()) {
            throw new BusinessValidationException("The following quotations have been expired: %s. PO cannot proceed"
                .formatted(expiredQuotations));
        }

        // All quotations must be in ACCEPTED state and have no POLine on them
        List<String> quotationsInInvalidState = quotations.stream()
            .filter(q -> q.getQuotationState() != QuotationStateEnum.ACCEPTED)
            .map(QuotationT::getPublicId)
            .toList();

        if (!quotationsInInvalidState.isEmpty()) {
            throw new BusinessValidationException("The following quotation(s) are not in 'ACCEPTED' state: %s. PO cannot proceed"
                .formatted(quotationsInInvalidState));
        }

        List<String> quotationHasAlreadyPOLine = quotations.stream()
            .filter(QuotationT::hasPOLine)
            .map(QuotationT::getPublicId)
            .toList();

        if (!quotationHasAlreadyPOLine.isEmpty()) {
            throw new BusinessValidationException("Quotation(s) with publicId: %s have already been already associated with a PO line. PO cannot proceed"
                .formatted(quotationHasAlreadyPOLine));
        }

        // All quotations must reference the same supplier
        Set<String> quotationsSupplier = quotations.stream()
            .map(QuotationT::getSupplier)
            .map(SupplierT::getCompanyName)
            .collect(Collectors.toSet());

        if (quotationsSupplier.size() > 1) {
            throw new BusinessValidationException(("Found more than one supplier in quotations provided." +
                "All quotations must reference the same supplier. PO cannot proceed"));
        }

        // Supplier must still be ACTIVE at PO creation
        SupplierT supplier = quotations.stream()
            .map(QuotationT::getSupplier)
            .findFirst()
            .orElseThrow();

        if (supplier.getStatus() == ResourceStatusEnum.INACTIVE) {
            throw new BusinessValidationException("Supplier with publicId: %s is inactive. PO cannot proceed"
                .formatted(supplier.getPublicId()));
        }

        // Source requisition must be in FINALIZED state &&
        // All quotations must reference lines belonging to the same parent requisition
        Set<String> reqLinesPublicIds = quotations.stream()
            .map(QuotationT::getReqLine)
            .map(RequisitionLineT::getPublicId)
            .collect(Collectors.toSet());

        List<RequisitionLineT> retrievedReqLines = requisitionLineService.getLinesAndValidateForQuotation(reqLinesPublicIds);

        return retrievedReqLines.stream()
            .collect(Collectors.toMap(RequisitionLineT::getPublicId, line -> line));
    }

    private void validateNumberOfQuotationsAgainstAmountAndPOType(Set<QuotationT> quotations,
                                                                  PurchaseOrderTypeEnum poType,
                                                                  String justificationNotes) {
        quotations.forEach(
            q -> {

                BigDecimal totalAmount = q.getUnitPrice().multiply(q.getQuotedQuantity()).setScale(2, RoundingMode.HALF_UP);

                if (totalAmount.compareTo(new BigDecimal(LOW_THRESHOLD)) > 0 &&
                    totalAmount.compareTo(new BigDecimal(HIGH_THRESHOLD)) <= 0 &&
                    poType != PurchaseOrderTypeEnum.URGENT) {
                    if (q.getReqLine().getQuotations().size() < 3 && !StringUtils.hasText(justificationNotes)) {
                        throw new BusinessValidationException("For requisitions lines that exceed 500.00 and PO is not of type 'URGENT'" +
                            " they must either have at least 3 quotations or you have to provide a justification when creating your PO");
                    }
                }

                if (totalAmount.compareTo(new BigDecimal(HIGH_THRESHOLD)) > 0 &&
                    poType != PurchaseOrderTypeEnum.URGENT) {
                    if (q.getReqLine().getQuotations().size() < 3) {
                        throw new BusinessValidationException("For requisitions lines that exceed 10,000.00 and PO is not of type 'URGENT'" +
                            "at least 3 quotations required for PO to proceed");
                    }
                }
            }
        );

        if (poType.equals(PurchaseOrderTypeEnum.URGENT) && !StringUtils.hasText(justificationNotes)) {
            throw new BusinessValidationException("In POs that are of type 'URGENT' you must always include justification notes");
        }
    }

    private void validateAllQuotationsExist(Set<String> requestedPublicIds, Set<String> retrievedPublicIds) {
        Set<String> missingPublicIds = new HashSet<>(requestedPublicIds);
        missingPublicIds.removeAll(retrievedPublicIds);

        if (!missingPublicIds.isEmpty()) {
            throw new ResourceNotFoundException("The following publicIds do not correspond to existing quotations: %s"
                .formatted(missingPublicIds));
        }
    }

    private CurrencyEnum fetchCurrencyAndValidateIsSame(Set<QuotationT> quotations) {
        Set<CurrencyEnum> currenciesFound = quotations.stream()
            .map(QuotationT::getCurrency)
            .collect(Collectors.toSet());

        if (currenciesFound.size() > 1) {
            throw new BusinessValidationException("Currency must be same for all quotations");
        }

        return currenciesFound.stream().findFirst().orElseThrow();
    }

    private PurchaseOrderT loadResourceAndValidateVersion(String poPublicId, Long providedVersion) {
        PurchaseOrderT purchaseOrder = purchaseOrderRepository.findByPublicId(poPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase order", "publicId", poPublicId));

        if (!Objects.equals(purchaseOrder.getVersion(), providedVersion)) {
            throw new OptimisticLockException("Another user has concurrently modified same resource. Please try again");
        }

        return purchaseOrder;
    }
}
