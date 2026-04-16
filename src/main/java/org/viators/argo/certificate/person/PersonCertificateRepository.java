package org.viators.argo.certificate.person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PersonCertificateRepository extends JpaRepository<PersonCertificateT, Long> {

    Optional<PersonCertificateT> findByPublicId(String publicId);

    Page<PersonCertificateT> findByPerson_PublicId(String personPublicId, Pageable pageable);

    boolean existsByCertificateNumber(String certificateNumber);

    @Query("""
           select count(c) from PersonCertificateT c
           where c.person.publicId = :personPublicId
             and c.expiryDate is not null
             and c.expiryDate < :today
           """)
    long countExpiredByPersonPublicId(
        @Param("personPublicId") String personPublicId,
        @Param("today") LocalDate today
    );

    @Query("""
           select count(c) from PersonCertificateT c
           where c.person.publicId = :personPublicId
             and c.expiryDate is not null
             and c.expiryDate >= :today
             and c.expiryDate < :horizon
           """)
    long countExpiringSoonByPersonPublicId(
        @Param("personPublicId") String personPublicId,
        @Param("today") LocalDate today,
        @Param("horizon") LocalDate horizon
    );

    @Query("""
           select count(c) from PersonCertificateT c
           where c.person.publicId = :personPublicId
             and (c.expiryDate is null or c.expiryDate >= :horizon)
           """)
    long countValidByPersonPublicId(
        @Param("personPublicId") String personPublicId,
        @Param("horizon") LocalDate horizon
    );
}
