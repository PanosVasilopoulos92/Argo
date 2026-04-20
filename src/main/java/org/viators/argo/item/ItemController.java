package org.viators.argo.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.item.dto.request.CreateItemRequest;
import org.viators.argo.item.dto.request.ItemSearchFilterRequest;
import org.viators.argo.item.dto.response.ItemDetailsResponse;
import org.viators.argo.item.dto.response.ItemSummaryResponse;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PreAuthorize("hasRole('PROCUREMENT')")
    @PostMapping
    public ResponseEntity<ItemDetailsResponse> create(@Valid @RequestBody CreateItemRequest request) {
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

    @GetMapping("/filter")
    public ResponseEntity<Page<ItemSummaryResponse>> getItemBasedOnFilters(
        @Valid @ModelAttribute ItemSearchFilterRequest request,
        @PageableDefault(sort = "itemCode", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<ItemSummaryResponse> response = itemService.getItemBasedOnFilters(request, pageable);
        return ResponseEntity.ok(response);
    }

}
