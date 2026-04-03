package org.viators.argo.vessel.dto.request;

import org.viators.argo.vessel.VesselT;
import org.viators.argo.vessel.enums.ClassificationSocietyEnum;
import org.viators.argo.vessel.enums.VesselTypeEnum;

import java.util.Optional;

public record UpdateVesselInfoRequest(
    String vesselName,
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
    String portOfRegistry
) {

    public void update(VesselT entity) {
        Optional.ofNullable(vesselName).ifPresent(entity::setVesselName);
        Optional.ofNullable(mmsiNumber).ifPresent(entity::setMmsiNumber);
        Optional.ofNullable(callSign).ifPresent(entity::setCallSign);
        Optional.ofNullable(flagState).ifPresent(entity::setFlagState);
        Optional.ofNullable(vesselType).ifPresent(entity::setVesselType);
        Optional.ofNullable(grossTonnage).ifPresent(entity::setGrossTonnage);
        Optional.ofNullable(netTonnage).ifPresent(entity::setNetTonnage);
        Optional.ofNullable(deadWeightTonnage).ifPresent(entity::setDeadWeightTonnage);
        Optional.ofNullable(yearBuild).ifPresent(entity::setYearBuild);
        Optional.ofNullable(builder).ifPresent(entity::setBuilder);
        Optional.ofNullable(classificationSociety).ifPresent(entity::setClassificationSociety);
        Optional.ofNullable(portOfRegistry).ifPresent(entity::setPortOfRegistry);
    }
}
