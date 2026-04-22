package org.viators.argo.requisition.sequence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequisitionSequenceRepository extends JpaRepository<RequisitionSequenceT, Integer> {

    Optional<RequisitionSequenceT> findTop1ByYearOrderByLastValueDesc(Integer year);
}
