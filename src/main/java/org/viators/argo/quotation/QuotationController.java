package org.viators.argo.quotation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.quotation.dto.request.BulkCreateQuotationsRequest;
import org.viators.argo.quotation.dto.request.CreateQuotationRequest;
import org.viators.argo.quotation.dto.request.SearchQuotationFilteredRequest;
import org.viators.argo.quotation.dto.response.QuotationDetailsResponse;
import org.viators.argo.quotation.dto.response.QuotationSummaryResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK ', 'PROCUREMENT_MANAGER')")
    @PostMapping
    public ResponseEntity<QuotationDetailsResponse> create(
        @Valid @RequestBody CreateQuotationRequest request
    ) {
        QuotationDetailsResponse response = quotationService.create(request);
        return ResponseEntity
            .created(URI.create("/api/v1/quotations/" + response.publicId()))
            .body(response);
    }

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK ', 'PROCUREMENT_MANAGER')")
    @PostMapping("/bulk")
    public ResponseEntity<List<QuotationDetailsResponse>> createBulk(
        @Valid @RequestBody BulkCreateQuotationsRequest request
    ) {
        List<QuotationDetailsResponse> response = quotationService.createBulk(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @GetMapping("/filtered")
    public ResponseEntity<Page<QuotationSummaryResponse>> getQuotationsFiltered(
        @ModelAttribute SearchQuotationFilteredRequest request,
        @SortDefault.SortDefaults({
            @SortDefault(sort = "unitPrice", direction = Sort.Direction.ASC),
            @SortDefault(sort = "validUntil", direction = Sort.Direction.DESC)
        })
        Pageable pageable
    ) {
        return ResponseEntity.ok(quotationService.getQuotationsFiltered(request, pageable));
    }

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @GetMapping("/{publicId}")
    public ResponseEntity<QuotationDetailsResponse> getQuotation(@PathVariable String publicId) {
        return ResponseEntity.ok(quotationService.getQuotation(publicId));
    }

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @GetMapping()
    public ResponseEntity<Page<QuotationSummaryResponse>> getQuotations(
        @PageableDefault
        @SortDefault.SortDefaults({
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC),
            @SortDefault(sort = "supplier.companyName", direction = Sort.Direction.ASC)
        }) Pageable pageable
    ) {
        return ResponseEntity.ok(quotationService.getQuotations(pageable));
    }

}
