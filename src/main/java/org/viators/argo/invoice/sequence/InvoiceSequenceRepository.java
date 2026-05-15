package org.viators.argo.invoice.sequence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequenceT, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InvoiceSequenceT> findFirstByYearOrderByLastValue(Integer year);
}
