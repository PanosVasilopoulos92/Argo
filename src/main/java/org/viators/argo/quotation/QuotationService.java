package org.viators.argo.quotation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.quotation.dto.request.BulkCreateQuotationsRequest;
import org.viators.argo.quotation.dto.request.CreateQuotationRequest;
import org.viators.argo.quotation.dto.response.QuotationDetailsResponse;
import org.viators.argo.requisition.RequisitionLineService;
import org.viators.argo.requisition.RequisitionLineT;
import org.viators.argo.supplier.SupplierService;
import org.viators.argo.supplier.SupplierT;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final RequisitionLineService requisitionLineService;
    private final SupplierService supplierService;

    @Transactional
    public QuotationDetailsResponse create(CreateQuotationRequest request) {
        RequisitionLineT requisitionLine = requisitionLineService.getLineAndValidateStatusAndStateForQuotation(
            request.requisitionLinePublicId());
        SupplierT supplier = supplierService.getActiveResource(request.supplierPublicId());

        QuotationT quotation = request.toEntity(requisitionLine, supplier);
        return QuotationDetailsResponse.from(quotationRepository.save(quotation));
    }

    @Transactional
    public List<QuotationDetailsResponse> createBulk(BulkCreateQuotationsRequest request) {
        SupplierT supplier = supplierService.getActiveResource(request.supplierPublicId());
        Set<String> reqLinesIds = validateLines(request.lineQuotations());

        Map<String, RequisitionLineT> linesByPublicId = requisitionLineService
            .getLinesAndValidateForQuotation(reqLinesIds)
            .stream()
            .collect(Collectors.toMap(RequisitionLineT::getPublicId, line -> line));

        return request.lineQuotations().stream()
            .map(lineQuotation -> {
                RequisitionLineT requisitionLine = linesByPublicId.get(lineQuotation.requisitionLinePublicId());
                QuotationT quotation = request.toEntity(supplier, lineQuotation);
                quotation.setLine(requisitionLine);
                return quotationRepository.save(quotation);
            })
            .map(QuotationDetailsResponse::from)
            .toList();
    }

    // Private helper methods
    private Set<String> validateLines(List<BulkCreateQuotationsRequest.LineQuotation> lineQuotations) {
        List<String> lineIds = lineQuotations.stream()
            .map(BulkCreateQuotationsRequest.LineQuotation::requisitionLinePublicId)
            .toList();

        Set<String> uniqueLineIds = lineQuotations.stream()
            .map(BulkCreateQuotationsRequest.LineQuotation::requisitionLinePublicId)
            .collect(Collectors.toSet());

        if (lineIds.size() != uniqueLineIds.size()) {
            throw new BusinessValidationException("You provided quotation request more than once for the same requisition line." +
                " Fix it and try again"
            );
        }

        return uniqueLineIds;
    }

}
