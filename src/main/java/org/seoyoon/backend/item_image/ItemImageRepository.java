package org.seoyoon.backend.item_image;

import org.seoyoon.backend.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    List<ItemImage> findByItem(Item item);
}
