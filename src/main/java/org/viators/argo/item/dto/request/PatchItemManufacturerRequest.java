package org.viators.argo.item.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
import org.viators.argo.item.ItemT;

import static org.viators.argo.common.util.PatchUtils.applyIfPresent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchItemManufacturerRequest {

    private JsonNullable<String> manufacturer = JsonNullable.undefined();

    private JsonNullable<String> partNumber = JsonNullable.undefined();

    @NotNull(message = "Version is required for resource updates")
    private Long version;

    public void update(ItemT entity) {
        applyIfPresent(manufacturer, entity::setManufacturer);
        applyIfPresent(partNumber, entity::setPartNumber);
    }
}
