package org.viators.argo.certificate.vessel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.viators.argo.vessel.VesselT;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface VesselCertificateRepository extends JpaRepository<VesselCertificateT, Long> {

    Page<VesselCertificateT> findByVessel_PublicId(String vesselPublicId, Pageable pageable);

    @Query("""
           select count(vc) from VesselCertificateT vc
           where vc.vessel.publicId = :vesselPublicId
           and (vc.expiryDate is null or vc.expiryDate > :horizon)
           """)
    long getValidVesselCertificates(@Param("vesselPublicId") String vesselPublicId,
                                   @Param("horizon") LocalDate horizon);

    @Query("""
           select count(vc) from VesselCertificateT vc
           where vc.vessel.publicId = :vesselPublicId
           and vc.expiryDate > :today
           and vc.expiryDate <= :horizon
           """)
    long getVesselCertificatesThatExpireSoon(@Param("vesselPublicId") String vesselPublicId,
                                             @Param("today") LocalDate today,
                                             @Param("horizon") LocalDate horizon);

    @Query("""
           select count(vc) from VesselCertificateT vc
           where vc.vessel.publicId = :vesselPublicId
           and vc.expiryDate < :horizon
           """)
    long getExpiredVesselCertificates(@Param("vesselPublicId") String vesselPublicId,
                                      @Param("horizon") LocalDate horizon);
}
