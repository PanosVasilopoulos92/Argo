package org.viators.argo.invoice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.config.CurrentKeycloakId;
import org.viators.argo.invoice.dto.request.AssociateInvoiceToPORequest;
import org.viators.argo.invoice.dto.request.CreateInvoiceRequest;
import org.viators.argo.invoice.dto.response.InvoiceDetailsResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDetailsResponse> create(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceDetailsResponse response = invoiceService.create(request);

        return ResponseEntity
            .created(URI.create("/api/v1/invoices/" + response.invoicePublicId()))
            .body(response);
    }

    @PatchMapping("/{invoicePublicId}/associate-po")
    public ResponseEntity<InvoiceDetailsResponse> associateInvoiceToPO(
        @CurrentKeycloakId String keycloakId,
        @PathVariable String invoicePublicId,
        @Valid @RequestBody AssociateInvoiceToPORequest request
    ) {
        InvoiceDetailsResponse response = invoiceService.associateInvoiceToPO(keycloakId, invoicePublicId, request);
    }
}
