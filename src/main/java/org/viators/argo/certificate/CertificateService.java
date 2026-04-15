package org.viators.argo.certificate;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.certificate.dto.request.UpdateCertificateRequest;
import org.viators.argo.certificate.dto.response.CertificateDetailsResponse;
import org.viators.argo.certificate.dto.response.CertificateOverviewResponse;
import org.viators.argo.common.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {

    private final CertificateRepository certificateRepository;

    @Transactional
    public CertificateDetailsResponse updateCertificate(String certificatePublicId, UpdateCertificateRequest request) {
        CertificateT certificate = certificateRepository.findByPublicId(certificatePublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Certificate", "publicId", certificatePublicId));

        if (!Objects.equals(request.getVersion(), certificate.getVersion())) {
            throw new OptimisticLockException("Certification was modified by another user. Please reload and try again.");
        }

        request.update(certificate);
        return CertificateDetailsResponse.from(certificateRepository.save(certificate));
    }


    @Transactional(readOnly = true)
    public Page<CertificateOverviewResponse> getCertifications(int daysUntilExpiry, Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDate expiredAt = today.plusDays(daysUntilExpiry);

        return certificateRepository.getCertificatesExpiringAt(today, expiredAt, pageable)
            .map(CertificateOverviewResponse::from);
    }
}
