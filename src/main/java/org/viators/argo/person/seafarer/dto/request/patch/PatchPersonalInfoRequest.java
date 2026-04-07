package org.viators.argo.person.seafarer.dto.request.patch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.viators.argo.common.enums.GenderEnum;
import org.viators.argo.person.seafarer.SeafarerT;

import java.time.LocalDate;

import static org.viators.argo.common.util.PatchUtils.applyIfPresent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchPersonalInfoRequest {

    @NotBlank(message = "Firstname cannot be blank")
    @Size(min = 3, max = 30, message = "Firstname must be between 3-30 characters long")
    private JsonNullable<String> firstName = JsonNullable.undefined();

    @NotBlank(message = "Lastname cannot be blank")
    @Size(min = 3, max = 30, message = "Lastname must be between 3-30 characters long")
    private JsonNullable<String> lastName = JsonNullable.undefined();

    @NotBlank(message = "Father's name cannot be blank")
    @Size(min = 3, max = 30, message = "Father's name must be between 3-30 characters long")
    private JsonNullable<String> fatherName = JsonNullable.undefined();

    @Size(min = 3, max = 30, message = "Mother's name must be between 3-30 characters long")
    private JsonNullable<String> motherName = JsonNullable.undefined();

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private JsonNullable<LocalDate> birthDate = JsonNullable.undefined();

    @Size(max = 20, message = "Birth place must be at most 20 characters long")
    private JsonNullable<String> birthPlace = JsonNullable.undefined();

    @NotNull(message = "Gender is required")
    private JsonNullable<GenderEnum> gender = JsonNullable.undefined();

    @NotBlank(message = "Nationality cannot be blank")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Nationality must be in form of ISO 3166-1 alpha-3")
    private JsonNullable<String> nationality = JsonNullable.undefined();

    @NotNull(message = "Version field is required for updates")
    private Long version;

    public void update(SeafarerT entity) {
        applyIfPresent(firstName, entity::setFirstName);
        applyIfPresent(lastName, entity::setLastName);
        applyIfPresent(fatherName, entity::setFatherName);
        applyIfPresent(motherName, entity::setMotherName);
        applyIfPresent(birthDate, entity::setBirthDate);
        applyIfPresent(birthPlace, entity::setBirthPlace);
        applyIfPresent(gender, entity::setGender);
        applyIfPresent(nationality, entity::setNationality);
    }
}
