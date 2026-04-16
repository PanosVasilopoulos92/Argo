package org.viators.argo.person.seafarer.dto.response;

import org.viators.argo.assignment.AssignmentStateEnum;
import org.viators.argo.assignment.AssignmentT;
import org.viators.argo.certificate.CertificateT;
import org.viators.argo.certificate.enums.CertificateStatusIndicatorEnum;
import org.viators.argo.certificate.person.PersonCertificateT;
import org.viators.argo.certificate.vessel.VesselCertificateT;
import org.viators.argo.common.enums.GenderEnum;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.person.seafarer.SeafarerT;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;
import org.viators.argo.vessel.VesselT;
import org.viators.argo.vessel.dto.response.VesselSummaryResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    Boolean hasActiveAssignment,
    String vesselNameOfAssignment,
    SeafarerRankEnum assignmentRank,
    Map<String, Long> certificates
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
            entity.getSbExpiryDate(),
            hasActiveAssignment(entity.getAssignments()),
            getAssignmentVessel(entity.getAssignments()),
            getAssignmentRank(entity.getAssignments()),
            defineCertificationValidation(entity.getCertificates())
        );
    }

    private static boolean hasActiveAssignment(Set<AssignmentT> assignments) {
        return assignments.stream()
            .anyMatch(a -> AssignmentStateEnum.ACTIVE.equals(a.getAssignmentState()));
    }

    public static String getAssignmentVessel(Set<AssignmentT> assignments) {
        return assignments.stream()
            .filter(a -> AssignmentStateEnum.ACTIVE.equals(a.getAssignmentState()))
            .findFirst()
            .map(AssignmentT::getVessel)
            .map(VesselT::getVesselName)
            .orElse(null);
    }

    public static SeafarerRankEnum getAssignmentRank(Set<AssignmentT> assignments) {
        return assignments.stream()
            .filter(a -> AssignmentStateEnum.ACTIVE.equals(a.getAssignmentState()))
            .findFirst()
            .map(AssignmentT::getAssignmentRank)
            .orElse(null);
    }

    private static Map<String, Long> defineCertificationValidation(Set<PersonCertificateT> certificates) {

        LocalDate today = LocalDate.now();
        Map<String, Long> result  = certificates.stream()
            .collect(Collectors.groupingBy(
                c -> classifyCertificate(c, today),
                Collectors.counting()
            ));

        Arrays.stream(CertificateStatusIndicatorEnum.values())
            .forEach(s -> result.putIfAbsent(s.name(), 0L));

        return result;
    }

    private static String classifyCertificate(PersonCertificateT c, LocalDate today) {

        if (c.getExpiryDate().isBefore(today)) {
            return CertificateStatusIndicatorEnum.EXPIRED.name();
        } else if (c.getExpiryDate().isBefore(today.plusDays(90))) {
            return CertificateStatusIndicatorEnum.EXPIRING_SOON.name();
        } else {
            return CertificateStatusIndicatorEnum.VALID.name();
        }
    }
}
