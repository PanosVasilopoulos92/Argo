package org.viators.argo.certificate.vessel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselCertificateRepository extends JpaRepository<VesselCertificateT, Long> {

    Page<VesselCertificateT> findByVessel_PublicId(String vesselPublicId, Pageable pageable);
}
