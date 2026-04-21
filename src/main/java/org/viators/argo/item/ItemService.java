package org.viators.argo.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.item.dto.request.CreateItemRequest;
import org.viators.argo.item.dto.request.ItemSearchFilterRequest;
import org.viators.argo.item.dto.request.PatchItemBasicInfoRequest;
import org.viators.argo.item.dto.request.PatchItemSupplierRequest;
import org.viators.argo.item.dto.response.ItemDetailsResponse;
import org.viators.argo.item.dto.response.ItemSummaryResponse;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;
import org.viators.argo.item.sequence.ItemCategorySequenceRepository;
import org.viators.argo.item.sequence.ItemCategorySequenceT;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemCategorySequenceRepository itemCategorySequenceRepository;

    @Transactional
    public ItemDetailsResponse create(CreateItemRequest request) {
        ItemT item = request.toEntity();
        item.setItemCode(generateItemCode(request.itemCategory()));

        item = itemRepository.save(item);
        return ItemDetailsResponse.from(item);
    }

    @Transactional
    public ItemDetailsResponse patchItemBasicInfo(String itemPublicId, PatchItemBasicInfoRequest request) {
        ItemT item = loadResourceAndCheckVersion(itemPublicId, request.getVersion());
        request.update(item);

        return ItemDetailsResponse.from(itemRepository.save(item));
    }

    @Transactional
    public ItemDetailsResponse patchItemSupplierInfo(String itemPublicId, PatchItemSupplierRequest request) {
        ItemT item = loadResourceAndCheckVersion(itemPublicId, request.getVersion());
        request.update(item);

        return ItemDetailsResponse.from(itemRepository.save(item));
    }

    @Transactional
    public void deactivateItem(String itemPublicId) {
        ItemT item = itemRepository.findByPublicId(itemPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Item", "publicId", itemPublicId));

        if (!Objects.equals(ResourceStatusEnum.ACTIVE, item.getStatus())) {
            throw new InvalidStateException("Item with public Id: %s is already deactivated."
                .formatted(itemPublicId));
        }

        item.setStatus(ResourceStatusEnum.INACTIVE);
    }

    @Transactional
    public void reactivateItem(String itemPublicId) {
        ItemT item = itemRepository.findByPublicId(itemPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Item", "publicId", itemPublicId));

        if (Objects.equals(ResourceStatusEnum.ACTIVE, item.getStatus())) {
            throw new InvalidStateException("Item with public Id: %s is already active."
                .formatted(itemPublicId));
        }

        item.setStatus(ResourceStatusEnum.ACTIVE);
    }

    // Read only methods
    @Transactional(readOnly = true)
    public ItemDetailsResponse getItem(String itemPublicId) {
        ItemT item = itemRepository.findByPublicId(itemPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Item", "publicId", itemPublicId));

        return ItemDetailsResponse.from(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getItemBasedOnFilters(ItemSearchFilterRequest request, Pageable pageable) {
        Specification<ItemT> specs = (root, query, cb) -> cb.conjunction();

        if (!request.includeInactiveItems()) {
            specs = ItemSpecs.isActive();
        }

        if (StringUtils.hasText(request.nameContaining())) {
            specs = specs.and(ItemSpecs.hasNameContaining(request.nameContaining()));
        }

        if (request.itemCategory() != null) {
            specs = specs.and(ItemSpecs.hasCategory(request.itemCategory()));
        }

        if (StringUtils.hasText(request.supplierContaining())) {
            specs = specs.and(ItemSpecs.hasSupplierContaining(request.supplierContaining()));
        }

        if (StringUtils.hasText(request.itemCode())) {
            specs = specs.and(ItemSpecs.hasItemCode(request.itemCode()));
        }

        if (StringUtils.hasText(request.partNumber())) {
            specs = specs.and(ItemSpecs.hasPartNumber(request.partNumber()));
        }

        return itemRepository.findAll(specs, pageable)
            .map(ItemSummaryResponse::from);
    }

    public Set<ItemCategoryEnum> getAvailableItemCategories() {
        return Arrays.stream(ItemCategoryEnum.values())
            .collect(Collectors.toSet());
    }

    public Set<UnitOfMeasurementEnum> getAllUnitsOfMeasurement() {
        return Arrays.stream(UnitOfMeasurementEnum.values())
            .collect(Collectors.toSet());
    }

    // Private helper methods
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
            .orElseThrow(() -> new ResourceNotFoundException(
                "No sequence row found for category: %s" + itemCategory.name())
            );

        long nextVal = sequence.getLastVal() + 1;
        sequence.setLastVal(nextVal);

        return itemCategory.getItemCategoryCode() + "-" + String.format("%05d", nextVal);
    }

    private ItemT loadResourceAndCheckVersion(String resourcePublicId, Long resourceVersion) {
        ItemT item = itemRepository.findByPublicId(resourcePublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Item", "publicId", resourcePublicId));

        if (!Objects.equals(item.getVersion(), resourceVersion)) {
            throw new InvalidStateException("Another user has already updated resource with public Id: %s. Please try again."
                .formatted(resourcePublicId));
        }

        return item;
    }
}
