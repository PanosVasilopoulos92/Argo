package org.viators.argo.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.viators.argo.item.enums.ItemCategoryEnum;

public interface ItemRepository extends JpaRepository<ItemT, Long> {

    @Query("""
        select i.itemCode from ItemT i
        where i.itemCategory = :itemCategory
        order by i.createdAt desc
        limit 1
        """)
    String getLatestItemCodeForCategory(@Param("itemCategory") ItemCategoryEnum itemCategory);
}
