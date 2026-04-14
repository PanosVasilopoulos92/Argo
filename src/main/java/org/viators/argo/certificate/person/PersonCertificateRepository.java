package org.viators.argo.certificate.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonCertificateRepository extends JpaRepository<PersonCertificateT, Long> {

    Optional<PersonCertificateT> findByPublicId(String publicId);

    boolean existsByCertificateNumber(String certificateNumber);
}
