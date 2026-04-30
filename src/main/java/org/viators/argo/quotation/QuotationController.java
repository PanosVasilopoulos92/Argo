package org.viators.argo.quotation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viators.argo.quotation.dto.request.BulkCreateQuotationsRequest;
import org.viators.argo.quotation.dto.request.CreateQuotationRequest;
import org.viators.argo.quotation.dto.response.QuotationDetailsResponse;

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


}
