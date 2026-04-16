package org.viators.argo.vessel.dto.response;

import org.viators.argo.assignment.AssignmentStateEnum;
import org.viators.argo.assignment.AssignmentT;
import org.viators.argo.certificate.enums.CertificateStatusIndicatorEnum;
import org.viators.argo.certificate.vessel.VesselCertificateT;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.vessel.VesselT;
import org.viators.argo.vessel.enums.ClassificationSocietyEnum;
import org.viators.argo.vessel.enums.VesselTypeEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public record VesselDetailsResponse(
    String publicId,
    String vesselName,
    String imoNumber,
    String mmsiNumber,
    String callSign,
    String flagState,
    VesselTypeEnum vesselType,
    Double grossTonnage,
    Double netTonnage,
    Double deadWeightTonnage,
    Integer yearBuild,
    String builder,
    ClassificationSocietyEnum classificationSociety,
    String portOfRegistry,
    ResourceStatusEnum status,
    Instant createdAt,
    Instant updatedAt,
    Long version,
    Integer activeAssignments,
    Map<String, Long> certificationsStatusIndicators
) {

    public static VesselDetailsResponse from(VesselT entity) {
        return new VesselDetailsResponse(
            entity.getPublicId(),
            entity.getVesselName(),
            entity.getImoNumber(),
            entity.getMmsiNumber(),
            entity.getCallSign(),
            entity.getFlagState(),
            entity.getVesselType(),
            entity.getGrossTonnage(),
            entity.getNetTonnage(),
            entity.getDeadWeightTonnage(),
            entity.getYearBuild(),
            entity.getBuilder(),
            entity.getClassificationSociety(),
            entity.getPortOfRegistry(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getVersion(),
            filterActiveAssignments(entity.getAssignments()),
            defineCertificationValidation(entity.getCertificates())
        );
    }

    private static Map<String, Long> defineCertificationValidation(Set<VesselCertificateT> certificates) {

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

    private static String classifyCertificate(VesselCertificateT c, LocalDate today) {

        if (c.getExpiryDate().isBefore(today)) {
            return CertificateStatusIndicatorEnum.EXPIRED.name();
        } else if (c.getExpiryDate().isBefore(today.plusDays(90))) {
            return CertificateStatusIndicatorEnum.EXPIRING_SOON.name();
        } else {
            return CertificateStatusIndicatorEnum.VALID.name();
        }
    }

    private static int filterActiveAssignments(Set<AssignmentT> assignments) {

        return assignments.stream()
            .filter(a -> AssignmentStateEnum.ACTIVE.equals(a.getAssignmentState()))
            .collect(Collectors.toSet())
            .size();
    }
}
