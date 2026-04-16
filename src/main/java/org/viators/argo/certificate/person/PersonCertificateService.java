package org.viators.argo.certificate.person;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.certificate.CertificateRepository;
import org.viators.argo.certificate.CertificateT;
import org.viators.argo.certificate.person.dto.request.CreatePersonCertificateRequest;
import org.viators.argo.certificate.person.dto.response.PersonCertificateSummaryResponse;
import org.viators.argo.common.enums.ReportPeriod;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.person.PersonRepository;
import org.viators.argo.person.PersonT;
import org.viators.argo.person.seafarer.SeafarerT;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonCertificateService {

    private final CertificateRepository certificateRepository;
    private final PersonCertificateRepository personCertificateRepository;
    private final PersonRepository personRepository;

    @Transactional
    public String create(CreatePersonCertificateRequest request) {

        PersonT person = personRepository.findByPublicId(request.personPublicId())
            .orElseThrow(() -> new ResourceNotFoundException("Person", "publicId", request.personPublicId()));

        if (certificateRepository.existsByCertificateNumber(request.certificateNumber())) {
            throw new DuplicateResourceException("Certificate", "certificateNumber", request.certificateNumber());
        }

        if (request.expiryDate() != null && request.expiryDate().isBefore(request.issueDate())) {
            throw new BusinessValidationException("Expiry date of certificate must be after issued date");
        }

        PersonCertificateT certificate = request.toEntity();
        person.addCertificate(certificate);

        personCertificateRepository.save(certificate);
        return certificate.getPublicId();
    }

    @Transactional(readOnly = true)
    public Page<PersonCertificateSummaryResponse> getCertificatesForPerson(String personPublicId, Pageable pageable) {
        return personCertificateRepository.findByPerson_PublicId(personPublicId, pageable)
            .map(PersonCertificateSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public long getValidCertificatesCountForPerson(String personPublicId) {
        LocalDate today = LocalDate.now();
        return personCertificateRepository.countValidByPersonPublicId(
            personPublicId, today.plusDays(ReportPeriod.NINETY.getDays())
        );
    }

    @Transactional(readOnly = true)
    public long getExpiringSoonCertificatesCountForPerson(String personPublicId) {
        LocalDate today = LocalDate.now();
        return personCertificateRepository.countExpiringSoonByPersonPublicId(
            personPublicId, today, today.plusDays(ReportPeriod.NINETY.getDays())
        );
    }

    @Transactional(readOnly = true)
    public long getExpiredCertificatesCountForPerson(String personPublicId) {
        LocalDate today = LocalDate.now();
        return personCertificateRepository.countExpiredByPersonPublicId(personPublicId, today);
    }
}
