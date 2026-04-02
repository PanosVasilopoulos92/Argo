package org.viators.argo.vessel.dto.request;

import jakarta.validation.constraints.*;
import org.springframework.security.core.parameters.P;
import org.viators.argo.vessel.VesselT;
import org.viators.argo.vessel.enums.ClassificationSocietyEnum;
import org.viators.argo.vessel.enums.VesselTypeEnum;

import java.util.Optional;

public record CreateVesselRequest(
    @NotBlank(message = "Vessel name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters long")
    String vesselName,

    @NotNull(message = "IMO number is required")
    @Pattern(regexp = "^\\d{7}$", message = "IMO number must be exactly 7 digits")
    String imoNumber,

    @Pattern(regexp = "^\\d{9}$", message = "Maritime Mobile Service Identity must be exactly 9 digits")
    String mmsiNumber,

    @Size(max = 10, message = "Call sign must be at most 10 characters long")
    String callSign,

    @NotBlank(message = "Flag State is required")
    @Pattern(regexp = "^[A-Z]{3}$")
    String flagState,

    @NotNull(message = "Vessel type is required")
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
    String portOfRegistry
) {

    public VesselT toEntity() {
        return new VesselT(
            vesselName,
            imoNumber,
            mmsiNumber,
            callSign,
            flagState,
            vesselType,
            grossTonnage,
            netTonnage,
            deadWeightTonnage,
            yearBuild,
            builder,
            classificationSociety,
            portOfRegistry
        );
    }
}
