package org.viators.argo.certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<CertificateT, Long> {

    boolean existsByCertificateNumber(String certificateNumber);
}
