package org.viators.argo.certificate.vessel.dto.response;

import org.viators.argo.certificate.enums.CertificateStatusIndicatorEnum;
import org.viators.argo.certificate.vessel.VesselCertificateT;
import org.viators.argo.certificate.vessel.enums.VesselCertificateTypeEnum;

import java.time.LocalDate;

public record VesselCertificateSummaryResponse(
    VesselCertificateTypeEnum vesselCertificateType,
    String certificateNumber,
    String issuingAuthority,
    LocalDate issueDate,
    LocalDate expiryDate,
    String certificateStatusIndicator
) {

    public static VesselCertificateSummaryResponse from(VesselCertificateT entity) {
        return new VesselCertificateSummaryResponse(
            entity.getCertificateType(),
            entity.getCertificateNumber(),
            entity.getIssuingAuthority(),
            entity.getIssueDate(),
            entity.getExpiryDate(),
            calcCertificationStatusIndicator(entity.getExpiryDate())
        );
    }

    private static String calcCertificationStatusIndicator(LocalDate expiryDate) {
        if (expiryDate == null) {
            return null;
        }

        if (expiryDate.isBefore(LocalDate.now())) {
            return CertificateStatusIndicatorEnum.EXPIRED.name();
        } else if (LocalDate.now().plusDays(90).isAfter(expiryDate)) {
            return CertificateStatusIndicatorEnum.EXPIRING_SOON.name();
        } else {
            return CertificateStatusIndicatorEnum.VALID.name();
        }
    }
}
