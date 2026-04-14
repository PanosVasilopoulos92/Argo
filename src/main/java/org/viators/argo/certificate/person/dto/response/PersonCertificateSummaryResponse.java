package org.viators.argo.certificate.person.dto.response;

import org.viators.argo.certificate.person.PersonCertificateT;
import org.viators.argo.certificate.person.PersonCertificateTypeEnum;

import java.time.LocalDate;

public record PersonCertificateSummaryResponse(
    PersonCertificateTypeEnum personCertificateType,
    String certificateNumber,
    String issuingAuthority,
    LocalDate issueDate,
    LocalDate expiryDate,
    String certificateStatusIndicator
) {

    public static PersonCertificateSummaryResponse from(PersonCertificateT entity) {
        return new PersonCertificateSummaryResponse(
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
            return "Expired";
        } else if (LocalDate.now().plusDays(90).isAfter(expiryDate)) {
            return "Expiring soon";
        } else {
            return "Valid";
        }
    }

}
