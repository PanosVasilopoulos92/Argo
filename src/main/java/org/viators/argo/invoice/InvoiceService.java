package org.viators.argo.invoice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.goodsreceipt.GoodsReceiptService;
import org.viators.argo.invoice.config.InvoiceToleranceProperties;
import org.viators.argo.invoice.dto.request.AssociateInvoiceToPORequest;
import org.viators.argo.invoice.dto.request.CreateInvoiceLineRequest;
import org.viators.argo.invoice.dto.request.CreateInvoiceRequest;
import org.viators.argo.invoice.dto.response.InvoiceDetailsResponse;
import org.viators.argo.invoice.dto.response.InvoiceLineSummaryResponse;
import org.viators.argo.invoice.enums.InvoiceStateEnum;
import org.viators.argo.invoice.enums.MatchStatusEnum;
import org.viators.argo.invoice.line.InvoiceLineT;
import org.viators.argo.invoice.sequence.InvoiceSequenceRepository;
import org.viators.argo.invoice.sequence.InvoiceSequenceT;
import org.viators.argo.purchaseorder.PurchaseOrderService;
import org.viators.argo.purchaseorder.PurchaseOrderT;
import org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum;
import org.viators.argo.purchaseorder.line.PurchaseOrderLineT;
import org.viators.argo.supplier.SupplierService;
import org.viators.argo.supplier.SupplierT;
import org.viators.argo.user.UserService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PurchaseOrderService purchaseOrderService;
    private final SupplierService supplierService;
    private final InvoiceSequenceRepository invoiceSequenceRepository;
    private final GoodsReceiptService goodsReceiptService;
    private final InvoiceToleranceProperties matchToleranceProperties;
    private final UserService userService;

    @Transactional
    public InvoiceDetailsResponse create(CreateInvoiceRequest request) {
        final String MATCH_MADE_BY_SYSTEM_AUTOMATICALLY = "SYSTEM";

        SupplierT supplier = supplierService.getActiveSupplier(request.supplierPublicId());

        validateCreate(request, supplier.getId());

        InvoiceT invoice = request.toEntity();
        invoice.setSupplier(supplier);
        invoice.setInvoiceNumber(generateInvoiceSequence());

        PurchaseOrderT purchaseOrder = new PurchaseOrderT();
        if (StringUtils.hasText(request.purchaseOrderPublicId())) {
            purchaseOrder = loadPOAndValidate(request.purchaseOrderPublicId(), supplier.getId(), request.currency());
            invoice.setPurchaseOrder(purchaseOrder);

        }

        for (CreateInvoiceLineRequest lineRequest : request.invoiceLines()) {
            InvoiceLineT line = buildInvoiceLine(lineRequest, purchaseOrder);
            invoice.addInvoiceLine(line);
        }

        if (purchaseOrder.getId() != null) {
            runMatchAndSetState(invoice, MATCH_MADE_BY_SYSTEM_AUTOMATICALLY);
        }

        InvoiceT savedInvoice = invoiceRepository.save(invoice);

        List<InvoiceLineSummaryResponse> responseLines = savedInvoice.getInvoiceLines().stream()
            .map(InvoiceLineSummaryResponse::from)
            .toList();

        return InvoiceDetailsResponse.from(savedInvoice, responseLines);
    }

    public InvoiceDetailsResponse associateInvoiceToPO(String keycloakId, String invoicePublicId, AssociateInvoiceToPORequest request) {
        String loggedInUser = userService.getUser(keycloakId).getUsername();

        InvoiceT invoice = invoiceRepository.findByPublicIdWithLines(invoicePublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", "publicId", invoicePublicId));

        if (invoice.getPurchaseOrder() != null) {
            throw new BusinessValidationException(
                "Invoice %s is already associated with PO %s"
                    .formatted(invoicePublicId, invoice.getPurchaseOrder().getPublicId())
            );
        }

        if (invoice.getInvoiceState() != InvoiceStateEnum.RECEIVED) {
            throw new BusinessValidationException("Invoice is not in state 'RECEIVED' and therefore it cannot get matched to a PO");
        }

        PurchaseOrderT po = loadPOAndValidate(
            request.purchaseOrderPublicId(),
            invoice.getSupplier().getId(),
            invoice.getCurrency()
        );
        invoice.setPurchaseOrder(po);

        Map<String, PurchaseOrderLineT> poLinesByPublicId = po.getPoLines().stream()
            .collect(Collectors.toMap(PurchaseOrderLineT::getPublicId, Function.identity()));

        Map<String, InvoiceLineT> invoiceLinesByPublicId = invoice.getInvoiceLines().stream()
            .collect(Collectors.toMap(InvoiceLineT::getPublicId, Function.identity()));

        request.lineAssociations().forEach((invoiceLinePublicId, poLinePublicId) -> {
            InvoiceLineT invoiceLine = Optional.ofNullable(invoiceLinesByPublicId.get(invoiceLinePublicId))
                .orElseThrow(() -> new BusinessValidationException(
                    "Invoice line %s not found on this invoice".formatted(invoiceLinePublicId)
                ));
            PurchaseOrderLineT poLine = Optional.ofNullable(poLinesByPublicId.get(poLinePublicId))
                .orElseThrow(() -> new BusinessValidationException(
                    "PO line %s does not belong to PO %s".formatted(poLinePublicId, po.getPublicId())
                ));

            poLine.addInvoiceLine(invoiceLine);
        });

        runMatchAndSetState(invoice, loggedInUser);

        InvoiceT saved = invoiceRepository.save(invoice);

        List<InvoiceLineSummaryResponse> responseLines = saved.getInvoiceLines().stream()
            .map(InvoiceLineSummaryResponse::from)
            .toList();

        return InvoiceDetailsResponse.from(saved, responseLines);
    }

    // Private helper methods
    private void validateCreate(CreateInvoiceRequest request, Long supplierDatabaseId) {
        List<CreateInvoiceLineRequest> invoiceLines = request.invoiceLines();

        if (invoiceLines.size() != new HashSet<>(invoiceLines).size()) {
            throw new DuplicateResourceException("You have provided same invoice line more than once");
        }

        validateTotalAmountOfInvoice(request);
        validatePoLinesProvidedMatchPO(request.purchaseOrderPublicId(), request.invoiceLines());

        if (invoiceRepository.existsBySupplierInvoiceReferenceAndSupplier_Id(request.supplierInvoiceReference(), supplierDatabaseId)) {
            throw new BusinessValidationException("Supplier's invoice reference: %s already exists in system"
                .formatted(request.supplierInvoiceReference()));
        }
    }

    private void validateTotalAmountOfInvoice(CreateInvoiceRequest request) {
        BigDecimal computedTotal = request.invoiceLines().stream()
            .map(line -> line.unitPrice().multiply(line.quantity()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (request.totalAmount().compareTo(computedTotal) != 0) {
            throw new BusinessValidationException(
                "Invoice total amount (%s) does not match the sum of line totals (%s)"
                    .formatted(request.totalAmount(), computedTotal)
            );
        }
    }

    private PurchaseOrderT loadPOAndValidate(String poPublicId, Long invoiceSupplierDatabaseId, CurrencyEnum invoiceCurrency) {

        PurchaseOrderT purchaseOrder = purchaseOrderService.getPurchaseOrder(poPublicId);

        if (!Objects.equals(purchaseOrder.getSupplier().getId(), invoiceSupplierDatabaseId)) {
            throw new BusinessValidationException("Invoice supplier and PO supplier defer. Please check and try again");
        }

        if (purchaseOrder.getCurrency() != invoiceCurrency) {
            throw new BusinessValidationException("Invoice currency defer from PO's currency. Invoice creation cannot proceed");
        }

        switch (purchaseOrder.getPurchaseOrderState()) {
            case PurchaseOrderStateEnum.DRAFT, PurchaseOrderStateEnum.SENT, PurchaseOrderStateEnum.CANCELLED ->
                throw new InvalidStateException("PO is in state '%s' and therefore cannot be used.".formatted(purchaseOrder.getPurchaseOrderState()));
        }

        return purchaseOrder;
    }

    private void validatePoLinesProvidedMatchPO(String poPublicId, List<CreateInvoiceLineRequest> providedInvoiceLines) {
        Set<String> providedPOLines = providedInvoiceLines.stream()
            .map(CreateInvoiceLineRequest::purchaseOrderLinePublicId)
            .collect(Collectors.toSet());

        if (!StringUtils.hasText(poPublicId)) {
            if (!providedPOLines.isEmpty()) {
                throw new BusinessValidationException("You have provided PO lines without providing a PO");
            }
            return; // No validation needed if user do not provide PO and PO lines
        }

        // Verify that provided PO lines belong to provided PO
        PurchaseOrderT purchaseOrderT = purchaseOrderService.getActivePO(poPublicId);
        Set<String> poLinesOfPOProvided = purchaseOrderT.getPoLines().stream()
            .map(PurchaseOrderLineT::getPublicId)
            .collect(Collectors.toSet());

        if (!poLinesOfPOProvided.containsAll(providedPOLines)) {
            throw new BusinessValidationException("You have provided PO lines that do not correspond to provided PO");
        }
    }

    private String generateInvoiceSequence() {
        int currentYear = LocalDate.now().getYear();
        InvoiceSequenceT latestInvoiceSequence = invoiceSequenceRepository.findFirstByYearOrderByLastValueDesc(currentYear)
            .orElse(new InvoiceSequenceT(currentYear, 0L, null));

        InvoiceSequenceT nextInvoiceSequence = new InvoiceSequenceT(
            currentYear, latestInvoiceSequence.getLastValue() + 1L, null
        );

        String finalFormattedValue = "INV-" + currentYear + "-" + String.format("%06d", nextInvoiceSequence.getLastValue());
        nextInvoiceSequence.setFinalFormattedValue(finalFormattedValue);
        invoiceSequenceRepository.save(nextInvoiceSequence);

        return finalFormattedValue;
    }

    private MatchStatusEnum matchInvoiceLineToPOLine(InvoiceLineT invoiceLine, PurchaseOrderLineT poLine) {
        BigDecimal totalReceivedQuantity = goodsReceiptService.computeTotalQuantityReceivedForPOLines(
            poLine.getPublicId(), BigDecimal.ZERO
        );

        BigDecimal pricePercent = computeVariancePercent(invoiceLine.getUnitPrice(), poLine.getUnitPrice());
        BigDecimal quantityPercent = computeVariancePercent(invoiceLine.getQuantity(), totalReceivedQuantity);

        invoiceLine.setPriceVariancePercent(pricePercent);
        invoiceLine.setQuantityVariancePercent(quantityPercent);

        // no receipts -> Unmatched status for invoice line
        if (quantityPercent == null) {
            return MatchStatusEnum.UNMATCHED;
        }

        boolean priceMismatch = pricePercent.abs().compareTo(matchToleranceProperties.priceTolerancePercent()) > 0;
        boolean quantityMismatch = quantityPercent.abs().compareTo(matchToleranceProperties.quantityTolerancePercent()) > 0;

        if (priceMismatch && quantityMismatch) {
            return MatchStatusEnum.BOTH_MISMATCH;
        }
        if (priceMismatch) {
            return MatchStatusEnum.PRICE_MISMATCH;
        }
        if (quantityMismatch) {
            return MatchStatusEnum.QUANTITY_MISMATCH;
        }

        return MatchStatusEnum.MATCHED;
    }

    private BigDecimal computeVariancePercent(BigDecimal actual, BigDecimal expected) {
        if (expected == null || expected.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return actual.subtract(expected)
            .divide(expected, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    private void runMatchAndSetState(InvoiceT invoice, String loggedInUser) {
        for (InvoiceLineT line : invoice.getInvoiceLines()) {
            if (line.getPoLine() == null) {
                line.setMatchStatus(MatchStatusEnum.UNMATCHED);
                continue;
            }
            MatchStatusEnum lineStatus = matchInvoiceLineToPOLine(line, line.getPoLine());
            line.setMatchStatus(lineStatus);
        }

        boolean anyMismatch = invoice.getInvoiceLines().stream()
            .anyMatch(l -> l.getMatchStatus() != MatchStatusEnum.MATCHED);

        invoice.setInvoiceState(anyMismatch ? InvoiceStateEnum.DISPUTED : InvoiceStateEnum.MATCHED);
        invoice.setMatchedAt(Instant.now());
        invoice.setMatchedBy(loggedInUser);
    }

    private InvoiceLineT buildInvoiceLine(CreateInvoiceLineRequest request, PurchaseOrderT po) {
        InvoiceLineT line = request.toEntity();

        if (po != null && StringUtils.hasText(request.purchaseOrderLinePublicId())) {
            PurchaseOrderLineT poLine = po.getPoLines().stream()
                .filter(pl -> pl.getPublicId().equals(request.purchaseOrderLinePublicId()))
                .findFirst()
                .orElseThrow(() -> new BusinessValidationException(
                    "PO line publicId %s does not belong to PO %s"
                        .formatted(request.purchaseOrderLinePublicId(), po.getPublicId())
                ));

            poLine.addInvoiceLine(line);
        }

        return line;
    }

}
