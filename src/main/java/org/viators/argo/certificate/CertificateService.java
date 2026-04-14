package org.viators.argo.certificate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.certificate.dto.response.CertificateOverviewResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {

    private final CertificateRepository certificateRepository;

    @Transactional(readOnly = true)
    public Page<CertificateOverviewResponse> getCertifications(Pageable pageable) {
        return certificateRepository.findAll(pageable)
            .map(CertificateOverviewResponse::from);
    }
}
