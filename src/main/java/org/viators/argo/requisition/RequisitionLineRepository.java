package org.viators.argo.requisition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionLineRepository extends JpaRepository<RequisitionLineT, Long> {


}
