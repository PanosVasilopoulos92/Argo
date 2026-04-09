package org.viators.argo.person.seafarer.dto.request;

import jakarta.validation.constraints.*;
import org.viators.argo.common.enums.GenderEnum;
import org.viators.argo.person.seafarer.SeafarerT;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

import java.time.LocalDate;

public record CreateSeafarerRequest(
    @NotBlank(message = "Firstname is required")
    @Size(min = 3, max = 30, message = "Firstname must be between 3-30 characters long")
    String firstName,

    @NotBlank(message = "Lastname is required")
    @Size(min = 3, max = 30, message = "Lastname must be between 3-30 characters long")
    String lastName,

    @NotBlank(message = "Father's name is required")
    @Size(min = 3, max = 30, message = "Father's name must be between 3-30 characters long")
    String fatherName,

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    LocalDate birthDate,

    @NotBlank(message = "Passport number is required")
    String passportNumber,

    @Past(message = "Passport issued date must be in past date")
    LocalDate passportIssuedDate,

    @NotNull(message = "Passport expiry date is required")
    @Future(message = "Passport expiry date must be in future date")
    LocalDate passportExpiryDate,

    @NotBlank(message = "Nationality is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Nationality must be in form of ISO 3166-1 alpha-3")
    String nationality,

    @NotBlank(message = "Seaman book number is required")
    String seamanBookNumber,

    @NotNull(message = "Seaman book issued date is required")
    @Past(message = "Seaman book issued date must be in the past")
    LocalDate sbIssuedAt,

    @NotNull(message = "Seaman book expiry date is required")
    @Future(message = "Seaman book expiry date must be a future one")
    LocalDate sbExpiryDate,

    @NotNull(message = "Seafarer rank is required")
    SeafarerRankEnum seafarerRank,

    @NotNull(message = "Gender is required")
    GenderEnum gender
) {

    public SeafarerT toEntity() {
        return SeafarerT.builder()
            .firstName(firstName)
            .lastName(lastName)
            .fatherName(fatherName)
            .birthDate(birthDate)
            .passportNumber(passportNumber)
            .passportIssuedDate(passportIssuedDate)
            .passportExpiryDate(passportExpiryDate)
            .nationality(nationality)
            .seamanBookNumber(seamanBookNumber)
            .sbIssuedAt(sbIssuedAt)
            .sbExpiryDate(sbExpiryDate)
            .rank(seafarerRank)
            .gender(gender)
            .build();
    }
}
