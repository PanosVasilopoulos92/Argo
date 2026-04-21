package org.viators.argo.item.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.item.ItemT;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;

import java.time.Instant;

public record ItemDetailsResponse(
    String itemPublicId,
    String name,
    String description,
    String itemCode,
    ItemCategoryEnum itemCategory,
    UnitOfMeasurementEnum unitOfMeasurement,
    String partNumber,
    String manufacturer,
    Instant createdAt,
    Long version,
    ResourceStatusEnum status
) {

    public static ItemDetailsResponse from(ItemT entity) {
        return new ItemDetailsResponse(
            entity.getPublicId(),
            entity.getName(),
            entity.getDescription(),
            entity.getItemCode(),
            entity.getItemCategory(),
            entity.getUnitOfMeasurement(),
            entity.getPartNumber(),
            entity.getManufacturer(),
            entity.getCreatedAt(),
            entity.getVersion(),
            entity.getStatus()
        );
    }
}
