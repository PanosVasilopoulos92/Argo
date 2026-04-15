package org.viators.argo.certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<CertificateT, Long> {

    Optional<CertificateT> findByPublicId(String publicId);

    boolean existsByCertificateNumber(String certificateNumber);
}
