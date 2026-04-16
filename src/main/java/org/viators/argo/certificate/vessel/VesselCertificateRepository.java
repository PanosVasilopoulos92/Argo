package org.viators.argo.certificate.vessel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.viators.argo.vessel.VesselT;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface VesselCertificateRepository extends JpaRepository<VesselCertificateT, Long> {

    Page<VesselCertificateT> findByVessel_PublicId(String vesselPublicId, Pageable pageable);

    Set<VesselCertificateT> getFindByVessel_PublicIdAndExpiryDateIsNullOrExpiryDateAfter(String vesselPublicId, LocalDate expiryDateBefore);

    Integer findByVessel_PublicIdAndExpiryDateBetween(String vesselPublicId, LocalDate expiryDateAfter, LocalDate expiryDateBefore);

    Integer findByVessel_PublicIdAndExpiryDateBefore(String vesselPublicId, LocalDate expiryDateBefore);
}
