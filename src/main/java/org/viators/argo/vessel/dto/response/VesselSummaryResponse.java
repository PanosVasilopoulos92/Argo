package org.viators.argo.vessel.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.vessel.VesselT;
import org.viators.argo.vessel.enums.VesselTypeEnum;

public record VesselSummaryResponse(
    String vesselName,
    String publicId,
    String imoNumber,
    VesselTypeEnum vesselType,
    String flagState,
    ResourceStatusEnum status
) {

    public static VesselSummaryResponse from(VesselT entity) {
        return new VesselSummaryResponse(
            entity.getVesselName(),
            entity.getPublicId(),
            entity.getImoNumber(),
            entity.getVesselType(),
            entity.getFlagState(),
            entity.getStatus()
        );
    }
}
