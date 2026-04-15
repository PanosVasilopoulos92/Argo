package org.viators.argo.certificate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<CertificateT, Long> {

    Optional<CertificateT> findByPublicId(String publicId);

    boolean existsByCertificateNumber(String certificateNumber);

    @Query("""
           select c from CertificateT c
           where c.expiryDate between :today and :expiredAt
           """)
    Page<CertificateT> getCertificatesExpiringAt(
        @Param("today") LocalDate today,
        @Param("expiredAt") LocalDate expiredAt,
        Pageable pageable);
}
