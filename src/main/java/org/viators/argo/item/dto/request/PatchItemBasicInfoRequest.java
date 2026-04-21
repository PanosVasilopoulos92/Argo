package org.viators.argo.item.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
import org.viators.argo.item.ItemT;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;

import static org.viators.argo.common.util.PatchUtils.applyIfPresent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchItemBasicInfoRequest {

    @Size(min = 2, max = 200, message = "Name must be between 2-200 characters long")
    private JsonNullable<String> name = JsonNullable.undefined();

    @Size(min = 2, max = 300, message = "Name must be between 2-300 characters long")
    private JsonNullable<String> description = JsonNullable.undefined();

    private JsonNullable<UnitOfMeasurementEnum> unitOfMeasurement = JsonNullable.undefined();

    @NotNull(message = "Version field is required for resource updates")
    private Long version;

    public void update(ItemT entity) {
        applyIfPresent(name, entity::setName);
        applyIfPresent(description, entity::setDescription);
        applyIfPresent(unitOfMeasurement, entity::setUnitOfMeasurement);
    }
}
