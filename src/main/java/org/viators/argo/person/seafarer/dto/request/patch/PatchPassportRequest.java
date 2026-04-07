package org.viators.argo.person.seafarer.dto.request.patch;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.viators.argo.person.seafarer.SeafarerT;

import java.time.LocalDate;

import static org.viators.argo.common.util.PatchUtils.applyIfPresent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchPassportRequest {

    @NotBlank(message = "Passport number must not be blank")
    private JsonNullable<String> passportNumber = JsonNullable.undefined();

    @Past(message = "Passport issued date must be in the past")
    private JsonNullable<LocalDate> passportIssued = JsonNullable.undefined();

    @Future(message = "Passport expiry date must be in the future")
    private JsonNullable<LocalDate> passportExpiryDate = JsonNullable.undefined();

    @NotNull(message = "Version field is required for updates")
    private Long version;

    public void update(SeafarerT entity) {
        applyIfPresent(passportNumber, entity::setPassportNumber);
        applyIfPresent(passportIssued, entity::setPassportIssued);
        applyIfPresent(passportExpiryDate, entity::setPassportExpiryDate);
    }
}
