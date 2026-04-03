package org.viators.argo.vessel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VesselRepository extends JpaRepository<VesselT, Long>, JpaSpecificationExecutor<VesselT> {

    Optional<VesselT> findByImoNumber(String imoNumber);

    Optional<VesselT> findByPublicId(String publicId);

    boolean existsByImoNumber(String imoNumber);

    boolean existsByMmsiNumber(String mmsiNumber);

    boolean existsByVesselName(String vesselName);

    boolean existsByCallSign(String callSign);
}
