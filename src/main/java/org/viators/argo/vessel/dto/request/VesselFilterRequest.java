package org.viators.argo.vessel.dto.request;

import org.viators.argo.vessel.enums.VesselTypeEnum;

public record VesselFilterRequest(
    String vesselNameContaining,
    VesselTypeEnum vesselType,
    String flagState,
    boolean includeInactiveVessels
) {
}
