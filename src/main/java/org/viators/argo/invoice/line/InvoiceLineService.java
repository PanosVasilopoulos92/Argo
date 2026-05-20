package org.viators.argo.invoice.line;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class InvoiceLineService {

    private final InvoiceLineRepository invoiceLineRepository;

    @Transactional(readOnly = true)
    public Set<InvoiceLineT> getByInvoiceWithPOLineAndReceipts(String invoicePublicId) {
        return invoiceLineRepository.findByInvoiceWithPOLineAndReceipts(invoicePublicId);
    }
}
