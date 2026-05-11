package org.viators.argo.requisition.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.viators.argo.item.ItemT;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;
import org.viators.argo.requisition.line.RequisitionLineT;

import java.math.BigDecimal;

public record CreateRequisitionLineRequest(
    @NotBlank(message = "Item public Id is required")
    String itemPublicId,

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive value")
    BigDecimal quantity,

    @NotNull(message = "Unit of measurement is required")
    UnitOfMeasurementEnum unitOfMeasurement,

    @Size(max = 500, message = "Remarks must be at most 500 characters long")
    String remarks
) {

    public RequisitionLineT toEntity(ItemT item) {
        return RequisitionLineT.builder()
            .quantity(quantity)
            .unitOfMeasurementEnum(unitOfMeasurement)
            .remarks(remarks)
            .catalogItem(item)
            .snapshotItemCategory(item.getItemCategory())
            .snapshotManufacturer(item.getManufacturer())
            .snapShotItemCode(item.getItemCode())
            .snapshotUnitOfMeasurement(unitOfMeasurement)
            .snapShotItemName(item.getName())
            .build();
    }
}
