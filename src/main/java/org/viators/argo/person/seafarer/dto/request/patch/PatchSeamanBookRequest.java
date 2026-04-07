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
public class PatchSeamanBookRequest {

    @NotBlank(message = "Seaman book number must not be blank")
    private JsonNullable<String> seamanBookNumber = JsonNullable.undefined();

    @Past(message = "Seaman book issued date must be in the past")
    private JsonNullable<LocalDate> sbIssuedAt = JsonNullable.undefined();

    @Future(message = "Seaman book expiry date must be in the future")
    private JsonNullable<LocalDate> sbExpiryDate = JsonNullable.undefined();

    @NotNull(message = "Version field is required for updates")
    private Long version;

    public void update(SeafarerT entity) {
        applyIfPresent(seamanBookNumber, entity::setSeamanBookNumber);
        applyIfPresent(sbIssuedAt, entity::setSbIssuedAt);
        applyIfPresent(sbExpiryDate,  entity::setSbExpiryDate);
    }
}
