package org.viators.argo.quotation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.quotation.dto.request.CreateQuotationRequest;
import org.viators.argo.quotation.dto.response.QuotationDetailsResponse;
import org.viators.argo.requisition.RequisitionLineService;
import org.viators.argo.requisition.RequisitionLineT;
import org.viators.argo.supplier.SupplierService;
import org.viators.argo.supplier.SupplierT;

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
}
