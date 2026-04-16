package org.viators.argo.person.seafarer.dto.response;

import lombok.Builder;
import org.viators.argo.assignment.dto.projection.ActiveAssignmentInfo;
import org.viators.argo.common.enums.GenderEnum;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.person.seafarer.SeafarerT;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

import java.time.Instant;
import java.time.LocalDate;

@Builder
public record SeafarerDetailsResponse(
    String publicId,
    ResourceStatusEnum status,
    Instant createdAt,
    Instant updatedAt,
    Long version,
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
    SeafarerRankEnum rank,
    String seamanBookNumber,
    LocalDate sbIssuedAt,
    LocalDate sbExpiryDate,
    boolean hasActiveAssignment,
    String vesselNameOfAssignment,
    SeafarerRankEnum assignmentRank,
    long validCertificatesCount,
    long expiringSoonCertificatesCount,
    long expiredCertificatesCount
) {

    public static SeafarerDetailsResponse from(
        SeafarerT entity,
        ActiveAssignmentInfo activeAssignment,
        long validCertificatesCount,
        long expiringSoonCertificatesCount,
        long expiredCertificatesCount
    ) {
        return SeafarerDetailsResponse.builder()
            .publicId(entity.getPublicId())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .version(entity.getVersion())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .fatherName(entity.getFatherName())
            .motherName(entity.getMotherName())
            .nationality(entity.getNationality())
            .birthDate(entity.getBirthDate())
            .birthPlace(entity.getBirthPlace())
            .gender(entity.getGender())
            .passportNumber(entity.getPassportNumber())
            .passportExpiryDate(entity.getPassportExpiryDate())
            .passportIssued(entity.getPassportIssuedDate())
            .remarks(entity.getRemarks())
            .bankName(entity.getBankName())
            .bankAccount(entity.getBankAccount())
            .rank(entity.getRank())
            .seamanBookNumber(entity.getSeamanBookNumber())
            .sbIssuedAt(entity.getSbIssuedAt())
            .sbExpiryDate(entity.getSbExpiryDate())
            .hasActiveAssignment(activeAssignment != null)
            .vesselNameOfAssignment(activeAssignment != null ? activeAssignment.vesselName() : null)
            .assignmentRank(activeAssignment != null ? activeAssignment.rank() : null)
            .validCertificatesCount(validCertificatesCount)
            .expiringSoonCertificatesCount(expiringSoonCertificatesCount)
            .expiredCertificatesCount(expiredCertificatesCount)
            .build();
    }
}
