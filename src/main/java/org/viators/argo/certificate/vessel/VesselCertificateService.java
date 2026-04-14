package org.viators.argo.certificate.vessel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.certificate.CertificateRepository;
import org.viators.argo.certificate.CertificateT;
import org.viators.argo.certificate.vessel.dto.request.CreateVesselCertificateRequest;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.vessel.VesselService;
import org.viators.argo.vessel.VesselT;

@Service
@RequiredArgsConstructor
@Slf4j
public class VesselCertificateService {

    private final VesselService vesselService;
    private final CertificateRepository certificateRepository;
    private final VesselCertificateRepository vesselCertificateRepository;

    @Transactional
    public String create(CreateVesselCertificateRequest request) {
        VesselT vessel = vesselService.getResourceByPublicId(request.vesselPublicId());

        if (certificateRepository.existsByCertificateNumber(request.certificateNumber())) {
            throw new DuplicateResourceException("Certificate", "certificateNumber", request.certificateNumber());
        }

        if (request.expiryDate() != null && request.expiryDate().isBefore(request.issueDate())) {
            throw new BusinessValidationException("Expiry date of certificate must be after issued date");
        }

        VesselCertificateT vesselCertificate = request.toEntity();
        vessel.addCertificate(vesselCertificate);
        vesselCertificateRepository.save(vesselCertificate);

        return vesselCertificate.getPublicId();
    }

}
