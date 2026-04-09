package org.viators.argo.person.seafarer.dto.response;

import org.viators.argo.common.enums.GenderEnum;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.person.seafarer.SeafarerT;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SeafarerDetailsResponse(
    // BaseEntity fields
    String publicId,
    ResourceStatusEnum status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long version,

    // PersonT fields
    String firstName,
    String lastName,
    String fatherName,
    String motherName,
    String nationality,
    LocalDate birthDate,
    String birthPlace,
    GenderEnum gender,
    String passportNumber,
    LocalDate passportExpiryDate,
    LocalDate passportIssued,
    String remarks,
    String bankName,
    String bankAccount,

    // SeafarerT fields
    SeafarerRankEnum rank,
    String seamanBookNumber,
    LocalDate sbIssuedAt,
    LocalDate sbExpiryDate
) {

    public static SeafarerDetailsResponse from(SeafarerT entity) {
        return new SeafarerDetailsResponse(
            entity.getPublicId(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getVersion(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getFatherName(),
            entity.getMotherName(),
            entity.getNationality(),
            entity.getBirthDate(),
            entity.getBirthPlace(),
            entity.getGender(),
            entity.getPassportNumber(),
            entity.getPassportExpiryDate(),
            entity.getPassportIssuedDate(),
            entity.getRemarks(),
            entity.getBankName(),
            entity.getBankAccount(),
            entity.getRank(),
            entity.getSeamanBookNumber(),
            entity.getSbIssuedAt(),
            entity.getSbExpiryDate()
        );
    }
}
