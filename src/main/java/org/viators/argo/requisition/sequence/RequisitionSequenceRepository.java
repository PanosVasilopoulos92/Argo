package org.viators.argo.requisition.sequence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequisitionSequenceRepository extends JpaRepository<RequisitionSequenceT, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RequisitionSequenceT> findTop1ByYearOrderByLastValueDesc(Integer year);
}
