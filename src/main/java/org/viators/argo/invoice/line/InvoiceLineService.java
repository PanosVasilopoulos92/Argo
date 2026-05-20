package org.viators.argo.invoice.line;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InvoiceLineService {

    private final InvoiceLineRepository invoiceLineRepository;

    @Transactional(readOnly = true)
    public Set<InvoiceLineT> getByInvoiceWithPOLineAndReceipts(String invoicePublicId) {
        return invoiceLineRepository.findByInvoiceWithPOLineAndReceipts(invoicePublicId);
    }

    @Transactional(readOnly = true)
    public boolean hasInvoiceLinesNotBelongingToCurrentInvoice(List<String> providedInvoiceLinesPublicIds, Long invoiceDatabaseId) {
        return invoiceLineRepository.existsByPublicIdInAndInvoice_Id(providedInvoiceLinesPublicIds, invoiceDatabaseId);
    }

    @Transactional(readOnly = true)
    public InvoiceLineT getInvoiceLine(String linePublicId) {
        return invoiceLineRepository.findByPublicId(linePublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice Line", "publicId", linePublicId));
    }

}
