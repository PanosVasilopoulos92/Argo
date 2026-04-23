package org.viators.argo.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.viators.argo.common.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemQueryService {

    private final ItemRepository itemRepository;

    public ItemT getResourcePublicId(String resourcePublicId) {
        return itemRepository.findByPublicId(resourcePublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Item", "publicId", resourcePublicId));
    }

    public ItemT getResourceByDatabaseId(Long resourceId) {
        return itemRepository.findById(resourceId)
            .orElseThrow(() -> new ResourceNotFoundException("Item", "id", resourceId));
    }

    public ItemT getItemByItemCode(String itemCode) {
        return itemRepository.findByPublicId(itemCode)
            .orElseThrow(() -> new ResourceNotFoundException("Item", "itemCode", itemCode));
    }
}
