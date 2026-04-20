package org.viators.argo.item.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.item.ItemT;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;

public record ItemSummaryResponse(
    String publicId,
    String name,
    String itemCode,
    String manufacturer,
    ItemCategoryEnum itemCategory,
    UnitOfMeasurementEnum unitOfMeasurement,
    ResourceStatusEnum status
) {

    public static ItemSummaryResponse from(ItemT entity) {
        return new ItemSummaryResponse(
            entity.getPublicId(),
            entity.getName(),
            entity.getItemCode(),
            entity.getManufacturer(),
            entity.getItemCategory(),
            entity.getUnitOfMeasurement(),
            entity.getStatus()
        );
    }
}
