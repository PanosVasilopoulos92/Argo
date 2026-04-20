package org.viators.argo.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viators.argo.item.dto.request.CreateItemRequest;
import org.viators.argo.item.dto.response.ItemDetailsResponse;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    public ResponseEntity<ItemDetailsResponse> createItem(@Valid @RequestBody CreateItemRequest request) {
        ItemDetailsResponse response = itemService.create(request);

        return ResponseEntity
            .created(URI.create("/api/v1/items/" + response.itemPublicId()))
            .body(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<Set<ItemCategoryEnum>> getAvailableItemCategories() {
        return ResponseEntity.ok(itemService.getAvailableItemCategories());
    }

    @GetMapping("/units-of-measurement")
    public ResponseEntity<Set<UnitOfMeasurementEnum>> getAllUnitsOfMeasurement() {
        return ResponseEntity.ok(itemService.getAllUnitsOfMeasurement());
    }

}
