package org.seoyoon.backend.item;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.seoyoon.backend.item_image.ItemImage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;
    private Long storeId;
    private String title;
    private String description;
    private String category;

    // 원가(매입가, KRW-원 단위 권장: Long), 정가(100% 가격), 현재 판매가, 현재 할인율(BPS: 1% = 100)
    private Long costPrice;          // 예: 25000
    private Long listPrice;          // 예: 39900 (정가)
    private Long salePrice;          // 예: 34900 (현재 판매가)
    private Integer discountRateBps; // 예: 1250 = 12.5%
    private Integer stock;

    @Enumerated(EnumType.STRING)
    private  SeasonType season;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemImage> images = new ArrayList<>();

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", costPrice=" + costPrice +
                ", listPrice=" + listPrice +
                ", salePrice=" + salePrice +
                ", discountRateBps=" + discountRateBps +
                ", stock=" + stock +
                ", season=" + season +
                '}';
    }
}

enum SeasonType {
    CURRENT,
    ARCHIVE
}
