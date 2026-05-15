package org.viators.argo.invoice.line;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceLineService {

    private final InvoiceLineRepository invoiceLineRepository;
}
