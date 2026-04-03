package org.viators.argo.vessel.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.viators.argo.vessel.VesselT;
import org.viators.argo.vessel.enums.ClassificationSocietyEnum;
import org.viators.argo.vessel.enums.VesselTypeEnum;

import java.util.Optional;

public record UpdateVesselInfoRequest(
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters long")
    String vesselName,

    @Pattern(regexp = "^\\d{9}$", message = "Maritime Mobile Service Identity must be exactly 9 digits")
    String mmsiNumber,

    @Size(max = 10, message = "Call sign must be at most 10 characters long")
    String callSign,

    @Pattern(regexp = "^[A-Z]{3}$")
    String flagState,

    VesselTypeEnum vesselType,

    @Positive(message = "Gross tonnage must be a positive number")
    Double grossTonnage,

    @Positive(message = "Net tonnage must be a positive number")
    Double netTonnage,

    @Positive(message = "Dead weight tonnage must be a positive number")
    Double deadWeightTonnage,

    Integer yearBuild,

    @Size(min = 3, max = 100, message = "Shipyard/Builder must be between 3-100 characters long")
    String builder,

    ClassificationSocietyEnum classificationSociety,

    @Size(min = 3, max = 100, message = "Port must be between 3-100 characters long")
    String portOfRegistry,

    @NotNull
    Long version
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
