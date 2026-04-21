package org.viators.argo.item.dto.request;

import jakarta.validation.constraints.Size;
import org.viators.argo.item.enums.ItemCategoryEnum;

public record ItemSearchFilterRequest(
    @Size(max = 200, message = "Name filter must be at most 200 characters long")
    String nameContaining,

    ItemCategoryEnum itemCategory,

    @Size(max = 100, message = "Supplier filter must be at most 100 characters long")
    String supplierContaining,

    @Size(max = 50, message = "Item code must be at most 50 characters long")
    String itemCode,

    @Size(max = 50, message = "Part number must be at most 50 characters long")
    String partNumber,

    boolean includeInactiveItems
) {
}
