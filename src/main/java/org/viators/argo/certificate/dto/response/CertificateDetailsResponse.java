package org.viators.argo.certificate.dto.response;

import org.viators.argo.certificate.CertificateT;
import org.viators.argo.certificate.person.PersonCertificateT;
import org.viators.argo.certificate.vessel.VesselCertificateT;
import org.viators.argo.common.enums.ResourceStatusEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record CertificateDetailsResponse(
    String certificatePublicId,
    String certificateNumber,
    String issuingAuthority,
    LocalDate issueDate,
    LocalDate expiryDate,
    String remarks,
    String certificateType,
    String certificateHolder,
    String certificateHolderType,
    Long daysUntilExpire,
    ResourceStatusEnum status,
    Instant createdAt,
    Long version
) {

    public static CertificateDetailsResponse from(CertificateT certificate) {
        String certificateType;
        String certificateHolder;
        String certificateHolderType;

        switch (certificate) {
            case PersonCertificateT pc -> {
                certificateType = pc.getCertificateType().toString();
                certificateHolder = pc.getPerson().getLastName() + " " + pc.getPerson().getFirstName();
                certificateHolderType = "Person";
            }
            case VesselCertificateT vc -> {
                certificateType = vc.getCertificateType().toString();
                certificateHolder = vc.getVessel().getVesselName();
                certificateHolderType = "Vessel";
            }
            default ->
                throw new IllegalArgumentException("Unknown certificate type: " + certificate.getClass().getSimpleName());
        }

        return new CertificateDetailsResponse(
            certificate.getPublicId(),
            certificate.getCertificateNumber(),
            certificate.getIssuingAuthority(),
            certificate.getIssueDate(),
            certificate.getExpiryDate(),
            certificate.getRemarks(),
            certificateType,
            certificateHolder,
            certificateHolderType,
            calcDaysUntilExpiry(certificate.getExpiryDate()),
            certificate.getStatus(),
            certificate.getCreatedAt(),
            certificate.getVersion()
        );
    }

    private static Long calcDaysUntilExpiry(LocalDate expiryDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
}
