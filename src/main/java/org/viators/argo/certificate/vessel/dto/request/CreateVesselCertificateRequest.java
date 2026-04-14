package org.viators.argo.certificate.vessel.dto.request;

import jakarta.validation.constraints.*;
import org.viators.argo.certificate.vessel.VesselCertificateT;
import org.viators.argo.certificate.vessel.enums.VesselCertificateTypeEnum;

import java.time.LocalDate;

public record CreateVesselCertificateRequest(

    @NotBlank(message = "Vessel public id is required")
    String vesselPublicId,

    @NotBlank(message = "Certificate number is required")
    @Size(max = 50, message = "Certificate number must be at most 50 characters long")
    String certificateNumber,

    @NotNull(message = "Certificate type is required")
    VesselCertificateTypeEnum certificateType,

    @NotBlank(message = "Issuing authority is required")
    @Size(max = 100, message = "Issuing authority must be at most 100 characters long")
    String issuingAuthority,

    @NotNull(message = "Issue date is required")
    @PastOrPresent(message = "Issue date must be today or in the past")
    LocalDate issueDate,

    LocalDate expiryDate,

    @Size(max = 500, message = "Remarks must be at most 500 characters long")
    String remarks
) {

    public VesselCertificateT toEntity() {
        return VesselCertificateT.builder()
            .certificateNumber(certificateNumber)
            .certificateType(certificateType)
            .issuingAuthority(issuingAuthority)
            .issueDate(issueDate)
            .expiryDate(expiryDate)
            .remarks(remarks)
            .build();
    }
}
