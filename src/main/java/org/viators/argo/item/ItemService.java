package org.viators.argo.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.sequence.ItemCategorySequenceRepository;
import org.viators.argo.common.sequence.ItemCategorySequenceT;
import org.viators.argo.item.dto.request.CreateItemRequest;
import org.viators.argo.item.dto.response.ItemDetailsResponse;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemCategorySequenceRepository itemCategorySequenceRepository;

    @Transactional(readOnly = true)
    public ItemDetailsResponse create(CreateItemRequest request) {
        ItemT item = request.toEntity();
        item.setItemCode(generateItemCode(request.itemCategory()));

        item = itemRepository.save(item);
        return ItemDetailsResponse.from(item);
    }

    public Set<ItemCategoryEnum> getAvailableItemCategories() {
        return Arrays.stream(ItemCategoryEnum.values())
            .collect(Collectors.toSet());
    }

    public Set<UnitOfMeasurementEnum> getAllUnitsOfMeasurement() {
        return Arrays.stream(UnitOfMeasurementEnum.values())
            .collect(Collectors.toSet());
    }

    /**
     * Generates the next item code for the given category.
     * Format: {PREFIX}-{zero-padded 5-digit sequence}
     * Example: LUB-00042
     *
     * @param itemCategory the item category
     * @return formatted item code, e.g. "LUB-00042"
     * @throws IllegalStateException if the category was not seeded in init-db.sql
     */
    private String generateItemCode(ItemCategoryEnum itemCategory) {
        ItemCategorySequenceT sequence = itemCategorySequenceRepository
            .findLockedByCategory(itemCategory)
            .orElseThrow(() -> new IllegalStateException(
                "No sequence row found for category: " + itemCategory));

        long nextVal = sequence.getLastVal() + 1;
        sequence.setLastVal(nextVal);

        return itemCategory.getItemCategoryCode() + "-" + String.format("%05d", nextVal);
    }
}
