package org.viators.argo.vessel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselRepository extends JpaRepository<VesselT, Long>, JpaSpecificationExecutor<VesselT> {

    boolean existsByImoNumber(String imoNumber);

    boolean existsByMmsiNumber(String mmsiNumber);
}
