package org.viators.argo.supplier;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.supplier.dto.request.CreateSupplierRequest;
import org.viators.argo.supplier.dto.request.PatchSupplierInfo;
import org.viators.argo.supplier.dto.request.SearchSupplierFiltersRequest;
import org.viators.argo.supplier.dto.response.SupplierDetailsResponse;
import org.viators.argo.supplier.dto.response.SupplierSummaryResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping("/filtered")
    public ResponseEntity<Page<SupplierSummaryResponse>> getFiltered(
        @ModelAttribute SearchSupplierFiltersRequest filters,
        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<SupplierSummaryResponse> response = supplierService.getByFilters(filters, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{supplierPublicId}")
    public ResponseEntity<SupplierDetailsResponse> getByPublicId(
        @PathVariable String supplierPublicId
    ) {
        SupplierDetailsResponse response = supplierService.getByPublicId(supplierPublicId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('PROCUREMENT_MANAGER')")
    @PatchMapping("/{supplierPublicId}/deactivate")
    public ResponseEntity<Void> deactivateSupplier(@PathVariable String supplierPublicId) {
        supplierService.deactivateSupplier(supplierPublicId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PROCUREMENT_MANAGER')")
    @PatchMapping("/{supplierPublicId}/reactivate")
    public ResponseEntity<Void> reactivateSupplier(@PathVariable String supplierPublicId) {
        supplierService.reactivateSupplier(supplierPublicId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PROCUREMENT_MANAGER')")
    @PostMapping
    public ResponseEntity<SupplierDetailsResponse> create(
        @Valid @RequestBody CreateSupplierRequest request
    ) {
        SupplierDetailsResponse response = supplierService.create(request);

        return ResponseEntity
            .created(URI.create("/api/v1/suppliers/" + response.publicId()))
            .body(response);
    }

    @PreAuthorize("hasRole('PROCUREMENT_MANAGER')")
    @PatchMapping("/{supplierPublicId}")
    public ResponseEntity<SupplierDetailsResponse> update(
        @PathVariable String supplierPublicId,
        @Valid @RequestBody PatchSupplierInfo request
    ) {
        SupplierDetailsResponse response = supplierService.updateInfo(supplierPublicId, request);
        return ResponseEntity.ok(response);
    }

}
