package org.viators.argo.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/categories")
    public ResponseEntity<Set<ItemCategoryEnum>> getAvailableItemCategories() {
        return ResponseEntity.ok(itemService.getAvailableItemCategories());
    }

    @GetMapping("/units-of-measurement")
    public ResponseEntity<Set<UnitOfMeasurementEnum>> getAllUnitsOfMeasurement() {
        return ResponseEntity.ok(itemService.getAllUnitsOfMeasurement());
    }

}
