package org.viators.argo.assignment.dto.response;

import org.viators.argo.assignment.AssignmentStateEnum;
import org.viators.argo.assignment.AssignmentT;
import org.viators.argo.certificate.person.PersonCertificateT;
import org.viators.argo.certificate.person.dto.response.PersonCertificateSummaryResponse;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public record AssignmentDetailsResponse(
    String assignmentPublicId,
    String seafarerPublicId,
    String seafarerFullName,
    String vesselPublicId,
    String vesselName,
    SeafarerRankEnum assignmentRank,
    LocalDate signOnDate,
    LocalDate expectedSignOffDate,
    LocalDate actualSignedOffDate,
    String signOnPort,
    String signOffPort,
    String remarks,
    String signOffRemarks,
    AssignmentStateEnum assignmentState,
    Set<PersonCertificateSummaryResponse> seafarerExpiredCertificates,
    Set<PersonCertificateSummaryResponse> seafarerNearExpirationCertificates
) {

    public static AssignmentDetailsResponse from(AssignmentT entity, Set<PersonCertificateT> certificates) {
        return new AssignmentDetailsResponse(
            entity.getPublicId(),
            entity.getSeafarer().getPublicId(),
            entity.getSeafarer().getFirstName() + " " + entity.getSeafarer().getLastName(),
            entity.getVessel().getPublicId(),
            entity.getVessel().getVesselName(),
            entity.getAssignmentRank(),
            entity.getSignOnDate(),
            entity.getExpectedSignOffDate(),
            entity.getActualSignedOffDate(),
            entity.getSignOnPort(),
            entity.getSignOffPort(),
            entity.getRemarks(),
            entity.getSignOffRemarks(),
            entity.getAssignmentState(),
            expiredCertificates(certificates),
            nearExpirationCertificates(certificates)
        );
    }

    private static Set<PersonCertificateSummaryResponse> expiredCertificates(Set<PersonCertificateT> certificates) {
        LocalDate today = LocalDate.now();

        return certificates.stream()
            .filter(c -> c.getExpiryDate() != null && c.getExpiryDate().isBefore(today))
            .map(PersonCertificateSummaryResponse::from)
            .collect(Collectors.toSet());
    }

    private static Set<PersonCertificateSummaryResponse> nearExpirationCertificates(Set<PersonCertificateT> certificates) {
        LocalDate today = LocalDate.now();

        return certificates.stream()
            .filter(c -> c.getExpiryDate() != null &&
                    !c.getExpiryDate().isBefore(today) &&
                    c.getExpiryDate().isBefore(today.plusDays(90)))
            .map(PersonCertificateSummaryResponse::from)
            .collect(Collectors.toSet());
    }
}
