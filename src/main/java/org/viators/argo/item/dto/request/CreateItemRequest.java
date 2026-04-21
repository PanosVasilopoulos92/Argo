package org.viators.argo.item.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.viators.argo.item.ItemT;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;

public record CreateItemRequest(
    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters long")
    String name,

    @Size(max = 300, message = "Description must be at most 300 characters long")
    String description,

    @NotNull(message = "Item category is required")
    ItemCategoryEnum itemCategory,

    @NotNull(message = "Unit of measurement is required")
    UnitOfMeasurementEnum unitOfMeasurement,

    @Size(max = 50, message = "Part number must be at most 50 characters long")
    String partNumber,

    @Size(max = 100, message = "Supplier must be at most 100 characters long")
    String supplier
) {

    public ItemT toEntity() {
        return ItemT.builder()
            .name(name)
            .description(description)
            .itemCategory(itemCategory)
            .unitOfMeasurement(unitOfMeasurement)
            .partNumber(partNumber)
            .supplier(supplier)
            .build();
    }
}
