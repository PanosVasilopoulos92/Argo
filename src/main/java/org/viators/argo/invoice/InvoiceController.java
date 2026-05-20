package org.viators.argo.invoice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.config.CurrentKeycloakId;
import org.viators.argo.invoice.dto.request.*;
import org.viators.argo.invoice.dto.response.DiscrepanciesSummaryResponse;
import org.viators.argo.invoice.dto.response.InvoiceDetailsResponse;
import org.viators.argo.invoice.dto.response.InvoiceDiscrepancyDetailsResponse;
import org.viators.argo.invoice.dto.response.InvoiceSummaryResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @PostMapping
    public ResponseEntity<InvoiceDetailsResponse> create(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceDetailsResponse response = invoiceService.create(request);

        return ResponseEntity
            .created(URI.create("/api/v1/invoices/" + response.invoicePublicId()))
            .body(response);
    }

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @PatchMapping("/{invoicePublicId}/associate-po")
    public ResponseEntity<InvoiceDetailsResponse> associateInvoiceToPO(
        @CurrentKeycloakId String keycloakId,
        @PathVariable String invoicePublicId,
        @Valid @RequestBody AssociateInvoiceToPORequest request
    ) {
        return ResponseEntity.ok(
            invoiceService.associateInvoiceToPO(keycloakId, invoicePublicId, request)
        );
    }

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @PatchMapping("/{invoicePublicId}/cancel")
    public ResponseEntity<Void> cancelInvoice(
        @CurrentKeycloakId String keycloakId,
        @PathVariable String invoicePublicId,
        @Valid @RequestBody CancelInvoiceRequest request
    ) {
        invoiceService.cancelInvoice(keycloakId, invoicePublicId, request);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PROCUREMENT_MANAGER')")
    @PatchMapping("/{invoicePublicId}/manual-match")
    public ResponseEntity<InvoiceDetailsResponse> overrideMatchingMechanism(
        @CurrentKeycloakId String keycloakId,
        @PathVariable String invoicePublicId,
        @Valid @RequestBody OverrideMatchMechanismRequest request
    ) {
        InvoiceDetailsResponse response = invoiceService.overrideMatchMechanism(keycloakId, invoicePublicId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{invoicePublicId}")
    public ResponseEntity<InvoiceDetailsResponse> getInvoice(@PathVariable String invoicePublicId) {
        return ResponseEntity.ok(
            invoiceService.getInvoice(invoicePublicId)
        );
    }

    @GetMapping("/filtered")
    public ResponseEntity<Page<InvoiceSummaryResponse>> getInvoicesFiltered(
        @ModelAttribute SearchInvoiceFilteredRequest filter,
        @PageableDefault(sort = "invoiceDate", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        Page<InvoiceSummaryResponse> response = invoiceService.getInvoicesFiltered(filter, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/discrepancy-summary")
    public ResponseEntity<List<DiscrepanciesSummaryResponse>> getDiscrepancySummaryByCurrency() {
        return ResponseEntity.ok(
            invoiceService.getDiscrepancySummaryByCurrency()
        );
    }

    @GetMapping("/{invoicePublicId}/discrepancy-details")
    public ResponseEntity<InvoiceDiscrepancyDetailsResponse> getDiscrepancyInvoiceWithLineDetails(
        @PathVariable String invoicePublicId
    ) {
        return ResponseEntity.ok(
            invoiceService.getDiscrepancyInvoiceWithLineDetails(invoicePublicId)
        );
    }
}
