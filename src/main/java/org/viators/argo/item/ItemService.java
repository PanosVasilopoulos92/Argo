package org.viators.argo.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.viators.argo.item.enums.ItemCategoryEnum;
import org.viators.argo.item.enums.UnitOfMeasurementEnum;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    public Set<ItemCategoryEnum> getAvailableItemCategories() {
        return Arrays.stream(ItemCategoryEnum.values())
            .collect(Collectors.toSet());
    }

    public Set<UnitOfMeasurementEnum> getAllUnitsOfMeasurement() {
        return Arrays.stream(UnitOfMeasurementEnum.values())
            .collect(Collectors.toSet());
    }

}
