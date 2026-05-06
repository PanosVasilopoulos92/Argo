package org.viators.argo.purchaseorder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.config.CurrentKeycloakId;
import org.viators.argo.purchaseorder.dto.request.AckPOFromSupplierRequest;
import org.viators.argo.purchaseorder.dto.request.ClosePORequest;
import org.viators.argo.purchaseorder.dto.request.CreatePORequest;
import org.viators.argo.purchaseorder.dto.request.SendPOToSupplierRequest;
import org.viators.argo.purchaseorder.dto.response.PODetailsResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @PostMapping
    public ResponseEntity<PODetailsResponse> create(@Valid @RequestBody CreatePORequest request) {
        PODetailsResponse response = purchaseOrderService.create(request);

        return ResponseEntity
            .created(URI.create("/api/v1/purchase-orders/" + response.poPublicId()))
            .body(response);
    }

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @PatchMapping("/{poPublicId}/send-to-supplier")
    public ResponseEntity<PODetailsResponse> sendPOToSupplier(
        @PathVariable String poPublicId,
        @Valid @RequestBody SendPOToSupplierRequest request
    ) {
        return ResponseEntity.ok(purchaseOrderService.sendPOToSupplier(poPublicId, request));
    }

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @PatchMapping("/{poPublicId}/acknowledge")
    public ResponseEntity<PODetailsResponse> acknowledgePOFromSupplier(
        @CurrentKeycloakId String keycloakId,
        @PathVariable String poPublicId,
        @Valid @RequestBody AckPOFromSupplierRequest request
    ) {
        return ResponseEntity.ok(purchaseOrderService.acknowledgePOFromSupplier(keycloakId, poPublicId, request));
    }

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @PatchMapping("/{poPublicId}/close")
    public ResponseEntity<PODetailsResponse> closePO(
        @CurrentKeycloakId String keycloakId,
        @PathVariable String poPublicId,
        @Valid @RequestBody ClosePORequest request
    ) {
        return ResponseEntity.ok(purchaseOrderService.closePO(keycloakId, poPublicId, request));
    }
}
