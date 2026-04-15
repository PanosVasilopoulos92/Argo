package org.viators.argo.certificate.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
import org.viators.argo.certificate.CertificateT;

import java.time.LocalDate;

import static org.viators.argo.common.util.PatchUtils.applyIfPresent;

@Getter
@Setter
public class UpdateCertificateRequest {

    @NotBlank(message = "Certificate number must not be blank")
    @Size(max = 50, message = "Certificate number must be at most 50 characters long")
    private JsonNullable<String> certificateNumber = JsonNullable.undefined();

    @NotBlank(message = "Issuing authority must not be blank")
    @Size(max = 100, message = "Issuing authority must be at most 100 characters long")
    private JsonNullable<String> issuingAuthority = JsonNullable.undefined();

    @NotNull(message = "Issue date must not be null")
    @PastOrPresent(message = "Issue date must be today or in the past")
    private JsonNullable<LocalDate> issueDate = JsonNullable.undefined();

    @Future(message = "Expiry date must be in a future date")
    private JsonNullable<LocalDate> expiryDate = JsonNullable.undefined();

    @Size(max = 500, message = "Remarks must be at most 500 characters long")
    private JsonNullable<String> remarks = JsonNullable.undefined();

    @NotNull(message = "Version is required")
    private Long version;

    public void update(CertificateT entity) {
        applyIfPresent(certificateNumber, entity::setCertificateNumber);
        applyIfPresent(issuingAuthority, entity::setIssuingAuthority);
        applyIfPresent(issueDate, entity::setIssueDate);
        applyIfPresent(expiryDate, entity::setExpiryDate);
        applyIfPresent(remarks, entity::setRemarks);
    }
}
