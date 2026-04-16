package org.viators.argo.vessel.dto.response;

import lombok.Builder;
import org.viators.argo.certificate.enums.CertificateStatusIndicatorEnum;
import org.viators.argo.certificate.vessel.VesselCertificateT;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.vessel.VesselT;
import org.viators.argo.vessel.enums.ClassificationSocietyEnum;
import org.viators.argo.vessel.enums.VesselTypeEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
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
    String vesselBuilder,
    ClassificationSocietyEnum classificationSociety,
    String portOfRegistry,
    ResourceStatusEnum status,
    Instant createdAt,
    Instant updatedAt,
    Long version,
    Integer activeAssignments,
    Integer validCertificatesCount,
    Integer expiringSoonCertificatesCount,
    Integer expiredCertificatesCount
) {

    public static VesselDetailsResponse from(VesselT entity, int activeAssignments, Map<String, Integer> certStats) {
        return VesselDetailsResponse.builder()
            .publicId(entity.getPublicId())
            .vesselName(entity.getVesselName())
            .imoNumber(entity.getImoNumber())
            .mmsiNumber(entity.getMmsiNumber())
            .callSign(entity.getCallSign())
            .flagState(entity.getFlagState())
            .vesselType(entity.getVesselType())
            .grossTonnage(entity.getGrossTonnage())
            .netTonnage(entity.getNetTonnage())
            .deadWeightTonnage(entity.getDeadWeightTonnage())
            .yearBuild(entity.getYearBuild())
            .vesselBuilder(entity.getBuilder())
            .classificationSociety(entity.getClassificationSociety())
            .portOfRegistry(entity.getPortOfRegistry())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .version(entity.getVersion())
            .activeAssignments(activeAssignments)
            .validCertificatesCount(certStats.get("validCertificatesCount"))
            .expiringSoonCertificatesCount(certStats.get("expiringSoonCertificatesCount"))
            .expiredCertificatesCount(certStats.get("expiredCertificatesCount"))
            .build();
    }
}
