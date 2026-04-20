package org.viators.argo.item;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.item.enums.ItemCategoryEnum;

@Component
public class ItemSpecs {

    public static Specification<ItemT> isActive() {
        return (root, query, cb) ->
            cb.equal(root.get("status"), ResourceStatusEnum.ACTIVE);
    }

    public static Specification<ItemT> hasNameContaining(String nameText) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("name")), "%" + nameText.toLowerCase() + "%");
    }

    public static Specification<ItemT> hasCategory(ItemCategoryEnum category) {
        return (root, query, cb) ->
            cb.equal(root.get("category"), category);
    }

    public static Specification<ItemT> hasManufacturerContaining(String manufacturerText) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("manufacturer")), "%" + manufacturerText.toLowerCase() + "%");
    }

    public static Specification<ItemT> hasItemCode(String itemCode) {
        return (root, query, cb) ->
            cb.equal(root.get("itemCode"), itemCode);
    }

    public static Specification<ItemT> hasPartNumber(String partNumber) {
        return (root, query, cb) ->
            cb.equal(root.get("partNumber"), partNumber);
    }
}
