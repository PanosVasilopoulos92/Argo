package org.viators.argo.certificate.vessel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselCertificateRepository extends JpaRepository<VesselCertificateT, Long> {

}
