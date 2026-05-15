package org.viators.argo.invoice.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceLineRepository extends JpaRepository<InvoiceLineT, Long> {
}
