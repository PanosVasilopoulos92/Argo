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
import org.viators.argo.invoice.dto.request.AssociateInvoiceToPORequest;
import org.viators.argo.invoice.dto.request.CreateInvoiceRequest;
import org.viators.argo.invoice.dto.request.SearchInvoiceFilteredRequest;
import org.viators.argo.invoice.dto.response.InvoiceDetailsResponse;
import org.viators.argo.invoice.dto.response.InvoiceSummaryResponse;

import java.net.URI;

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

    @GetMapping("/{invoicePublicId}")
    public ResponseEntity<InvoiceDetailsResponse> getInvoice(@PathVariable String invoicePublicId) {
        return ResponseEntity.ok(invoiceService.getInvoice(invoicePublicId));
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
}
