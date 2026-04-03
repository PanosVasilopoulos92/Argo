package org.viators.argo.vessel.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.vessel.VesselT;
import org.viators.argo.vessel.enums.ClassificationSocietyEnum;
import org.viators.argo.vessel.enums.VesselTypeEnum;

import java.time.LocalDateTime;

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
    LocalDateTime createdAt,
    LocalDateTime updatedAt
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
            entity.getUpdatedAt()
        );
    }
}
