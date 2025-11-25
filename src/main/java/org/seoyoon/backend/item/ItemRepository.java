package org.seoyoon.backend.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByStoreId(Long storeId);
    List<Item> findByStoreIdAndSeason(Long storeId, SeasonType season);

    @Query("SELECT DISTINCT i.category FROM Item i WHERE i.storeId = :storeId")
    List<String> findDistinctCategories(@Param("storeId") Long storeId);
}
