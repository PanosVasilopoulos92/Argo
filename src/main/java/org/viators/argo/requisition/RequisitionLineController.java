package org.viators.argo.requisition;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viators.argo.quotation.QuotationService;
import org.viators.argo.quotation.dto.response.QuotationDetailsResponse;
import org.viators.argo.quotation.dto.response.QuotationSummaryResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition-lines")
@RequiredArgsConstructor
public class RequisitionLineController {

    private final RequisitionLineRepository requisitionLineRepository;
    private final QuotationService quotationService;

    @GetMapping("/{linePublicId/quotations}")
    public ResponseEntity<List<QuotationSummaryResponse>> getQuotationsForLine(
        @PathVariable String linePublicId
    ) {
        return ResponseEntity.ok(quotationService.getQuotationsForLine(linePublicId));
    }
}
