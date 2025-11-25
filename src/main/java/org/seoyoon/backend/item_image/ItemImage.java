package org.seoyoon.backend.item_image;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.seoyoon.backend.item.Item;

@Getter
@Setter
@Entity
public class ItemImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemImageId;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "item_id", nullable = false)
//    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @JsonBackReference
    private Item item;
    @Column(length = 1000) // 🔥 최소 500~1000 추천
    private String url;
    private Integer sortOrder;

    @Override
    public String toString() {
        return this.url  + this.sortOrder;
    }
}
