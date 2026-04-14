package org.viators.argo.certificate.person.dto.request;

import jakarta.validation.constraints.*;
import org.viators.argo.certificate.person.PersonCertificateT;
import org.viators.argo.certificate.person.PersonCertificateTypeEnum;

import java.time.LocalDate;

public record CreatePersonCertificateRequest(

    @NotBlank(message = "Person public id is required")
    String personPublicId,

    @NotNull(message = "Certificate type is required")
    PersonCertificateTypeEnum certificateType,

    @NotBlank(message = "Certificate number is required")
    @Size(max = 50, message = "Certificate number must be at most 50 characters long")
    String certificateNumber,

    @NotBlank(message = "Issuing authority is required")
    @Size(max = 100, message = "Issuing authority must be at most 100 characters long")
    String issuingAuthority,

    @NotNull(message = "Issue date is required")
    @PastOrPresent(message = "Issue date must be today or in the past")
    LocalDate issueDate,

    @Future(message = "Expiry date must be in a future date")
    LocalDate expiryDate,

    @Size(max = 500, message = "Remarks must be at most 500 characters long")
    String remarks
) {

    public PersonCertificateT toEntity() {
        return PersonCertificateT.builder()
            .certificateType(certificateType)
            .certificateNumber(certificateNumber)
            .issuingAuthority(issuingAuthority)
            .issueDate(issueDate)
            .expiryDate(expiryDate)
            .remarks(remarks)
            .build();
    }
}
