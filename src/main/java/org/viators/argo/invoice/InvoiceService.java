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
import org.viators.argo.goodsreceipt.GoodsReceiptService;
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
import org.viators.argo.purchaseorder.line.PurchaseOrderLineService;
import org.viators.argo.purchaseorder.line.PurchaseOrderLineT;
import org.viators.argo.supplier.SupplierService;
import org.viators.argo.supplier.SupplierT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderLineService purchaseOrderLineService;
    private final SupplierService supplierService;
    private final InvoiceSequenceRepository invoiceSequenceRepository;
    private final GoodsReceiptService goodsReceiptService;

    @Transactional
    public InvoiceDetailsResponse create(CreateInvoiceRequest request) {
        InvoiceT invoice = request.toEntity();
        SupplierT supplier = supplierService.getActiveSupplier(request.supplierPublicId());

        validateCreate(request, supplier.getId());

        PurchaseOrderT purchaseOrder = loadPOAndValidate(request.purchaseOrderPublicId(), supplier.getId(), request.currency());

        invoice.setInvoiceNumber(generateInvoiceSequence());
        invoice.setInvoiceState(matchInvoiceToPOEngine(purchaseOrder.getId()));

        if (invoice.getInvoiceState() == InvoiceStateEnum.MATCHED) {
            invoice.setMatchedAt(Instant.now());
            invoice.setMatchedBy("SYSTEM");
        }

        request.invoiceLines().forEach(invLineRequest -> {
                InvoiceLineT invoiceLine = new InvoiceLineT();

                BigDecimal invoiceLineTotal = invLineRequest.unitPrice()
                    .multiply(invLineRequest.quantity())
                    .setScale(2, RoundingMode.HALF_UP);

                invoiceLine.setLineTotal(invoiceLineTotal);
                invoiceLine.setUnitPrice(invLineRequest.unitPrice());
                invoiceLine.setQuantity(invLineRequest.quantity());
                invoiceLine.setDescription(invLineRequest.description());

                if (invoice.getInvoiceState() == InvoiceStateEnum.MATCHED) {
                    PurchaseOrderLineT poLine = purchaseOrderLineService.findByPublicId(invLineRequest.purchaseOrderLinePublicId());
                    invoiceLine.setMatchStatus(matchInvoiceLineToPOLineEngine(poLine.getPublicId(), invLineRequest));
                    invoiceLine.setPriceVariance(calculatePriceVarianceBetweenPOLineAndInvoiceLine(
                        poLine.getUnitPrice(), invLineRequest.unitPrice())
                    );
                    invoiceLine.setQuantityVariance(calculateQuantityVarianceBetweenPOLineAndInvoiceLine(
                        poLine.getPublicId(), invLineRequest.quantity())
                    );
                    poLine.addInvoiceLine(invoiceLine);
                }

                invoice.addInvoiceLine(invoiceLine);
            }
        );

        List<InvoiceLineT> unmatchedInvoiceLines = invoice.getInvoiceLines().stream()
            .filter(invoiceLineT -> invoiceLineT.getMatchStatus() != MatchStatusEnum.MATCHED)
            .toList();

        if (!unmatchedInvoiceLines.isEmpty()) {
            invoice.setInvoiceState(InvoiceStateEnum.DISPUTED);
        }

        InvoiceT savedInvoice = invoiceRepository.save(invoice);

        List<InvoiceLineSummaryResponse> invoiceLines = savedInvoice.getInvoiceLines().stream()
            .map(InvoiceLineSummaryResponse::from)
            .toList();

        return InvoiceDetailsResponse.from(invoice, invoiceLines);
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

    private InvoiceStateEnum matchInvoiceToPOEngine(Long poDatabaseId) {
        PurchaseOrderT purchaseOrder = purchaseOrderService.getActivePOByDatabaseId(poDatabaseId);
        return purchaseOrder != null ? InvoiceStateEnum.MATCHED : InvoiceStateEnum.RECEIVED;
    }

    private MatchStatusEnum matchInvoiceLineToPOLineEngine(String poLinePublicId, CreateInvoiceLineRequest invoiceLine) {

        PurchaseOrderLineT poLine = purchaseOrderLineService.findByPublicId(poLinePublicId);
        BigDecimal invoiceLineTotal = invoiceLine.unitPrice()
            .multiply(invoiceLine.quantity())
            .setScale(2, RoundingMode.HALF_UP);

        if (poLine.getQuantity().compareTo(invoiceLine.quantity()) != 0 &&
            poLine.getUnitPrice().compareTo(invoiceLine.unitPrice()) != 0) {
            return MatchStatusEnum.BOTH_MISMATCH;
        }

        if (poLine.getQuantity().compareTo(invoiceLine.quantity()) != 0) {
            return MatchStatusEnum.QUANTITY_MISMATCH;
        }

        if (poLine.getUnitPrice().compareTo(invoiceLine.unitPrice()) != 0) {
            return MatchStatusEnum.PRICE_MISMATCH;
        }

        if (invoiceLineTotal.compareTo(poLine.getLineTotal()) == 0) {
            return MatchStatusEnum.MATCHED;
        }

        return MatchStatusEnum.UNMATCHED;
    }

    private BigDecimal calculatePriceVarianceBetweenPOLineAndInvoiceLine(BigDecimal poLineUnitPrice, BigDecimal invoiceLinePrice) {
        if (poLineUnitPrice.compareTo(invoiceLinePrice) != 0) {
            return poLineUnitPrice.compareTo(invoiceLinePrice) > 0
                ? poLineUnitPrice.subtract(invoiceLinePrice)
                : invoiceLinePrice.subtract(poLineUnitPrice);
        } else {
            return BigDecimal.ZERO;
        }

    }

    private BigDecimal calculateQuantityVarianceBetweenPOLineAndInvoiceLine(String poLinePublicId, BigDecimal invoiceLineQuantity) {
        BigDecimal receivedQuantity = goodsReceiptService.computeTotalQuantityReceivedForPOLines(poLinePublicId, BigDecimal.ZERO);

        if (receivedQuantity.compareTo(invoiceLineQuantity) != 0) {
            return receivedQuantity.compareTo(invoiceLineQuantity) > 0
                ? receivedQuantity.subtract(invoiceLineQuantity)
                : invoiceLineQuantity.subtract(receivedQuantity);
        } else {
            return BigDecimal.ZERO;
        }
    }
}
