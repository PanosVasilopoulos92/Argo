package org.viators.argo.certificate.dto.response;

import org.viators.argo.certificate.CertificateT;
import org.viators.argo.certificate.person.PersonCertificateT;
import org.viators.argo.certificate.vessel.VesselCertificateT;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record CertificateOverviewResponse(
    String certificateHolderName,
    String holderType,
    String certificateType,
    String certificateNumber,
    LocalDate expiryDate,
    Long daysUntilExpire
) {

    public static CertificateOverviewResponse from(CertificateT entity) {
        String holderName;
        String holderType;
        String certificateType;

        switch (entity) {
            case PersonCertificateT pc -> {
                holderName = pc.getPerson().getFirstName() + " " + pc.getPerson().getLastName();
                holderType = "PERSON";
                certificateType = pc.getCertificateType().name();
            }
            case VesselCertificateT vc -> {
                holderName = vc.getVessel().getVesselName();
                holderType = "VESSEL";
                certificateType = vc.getCertificateType().name();
            }
            default -> throw new IllegalArgumentException("Unknown certificate type: " + entity.getClass().getSimpleName());
        }

        return new CertificateOverviewResponse(
            holderName,
            holderType,
            certificateType,
            entity.getCertificateNumber(),
            entity.getExpiryDate(),
            calcDaysUntilExpire(entity.getExpiryDate())
        );
    }

    private static Long calcDaysUntilExpire(LocalDate expiryDate) {
        if (expiryDate == null) {
            return null;
        }

        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
}
