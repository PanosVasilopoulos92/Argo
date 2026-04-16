package org.viators.argo.certificate.vessel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.certificate.CertificateRepository;
import org.viators.argo.certificate.vessel.dto.request.CreateVesselCertificateRequest;
import org.viators.argo.certificate.vessel.dto.response.VesselCertificateSummaryResponse;
import org.viators.argo.common.enums.ReportPeriod;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.vessel.VesselQueryService;
import org.viators.argo.vessel.VesselService;
import org.viators.argo.vessel.VesselT;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VesselCertificateService {

    private final VesselQueryService vesselQueryService;
    private final CertificateRepository certificateRepository;
    private final VesselCertificateRepository vesselCertificateRepository;

    @Transactional
    public String create(CreateVesselCertificateRequest request) {
        VesselT vessel = vesselQueryService.getResourceByPublicId(request.vesselPublicId());

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

    @Transactional(readOnly = true)
    public Page<VesselCertificateSummaryResponse> getCertificatesForVessel(String vesselPublicId, Pageable pageable) {
        return vesselCertificateRepository.findByVessel_PublicId(vesselPublicId, pageable)
            .map(VesselCertificateSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public int getValidCertificatesForVesselCount(String vesselPublicId) {
        LocalDate today = LocalDate.now();
        return vesselCertificateRepository
            .getFindByVessel_PublicIdAndExpiryDateIsNullOrExpiryDateAfter(vesselPublicId, today).stream()
            .filter(c -> c.getExpiryDate().isAfter(today.plusDays(ReportPeriod.NINETY.getDays())))
            .collect(Collectors.toSet())
            .size();
    }

    @Transactional(readOnly = true)
    public int getExpiringSoonCertificatesCount(String vesselPublicId) {
        LocalDate today = LocalDate.now();
        return vesselCertificateRepository.findByVessel_PublicIdAndExpiryDateBetween(
            vesselPublicId, today, today.plusDays(ReportPeriod.NINETY.getDays())
        );
    }

    @Transactional(readOnly = true)
    public int getExpiredCertificatesCount(String vesselPublicId) {
        LocalDate today = LocalDate.now();
        return vesselCertificateRepository.findByVessel_PublicIdAndExpiryDateBefore(vesselPublicId, today);
    }

}
