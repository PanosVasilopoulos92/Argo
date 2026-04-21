package org.viators.argo.item.sequence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.viators.argo.item.enums.ItemCategoryEnum;

import java.util.Optional;

public interface ItemCategorySequenceRepository extends JpaRepository<ItemCategorySequenceT, ItemCategoryEnum> {

    /**
     * Fetches the sequence row for the given category and locks it
     * for update (SELECT ... FOR UPDATE), blocking any concurrent
     * transaction attempting to read the same row until this
     * transaction commits or rolls back.
     *
     * @param category the item category whose counter to lock
     * @return the locked sequence row, or empty if the category
     *         was not seeded in init-db.sql
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           SELECT s FROM ItemCategorySequenceT s
           WHERE s.category = :category
           """)
    Optional<ItemCategorySequenceT> findLockedByCategory(@Param("category") ItemCategoryEnum category);
}
