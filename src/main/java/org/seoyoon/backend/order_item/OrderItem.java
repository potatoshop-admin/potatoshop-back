package org.seoyoon.backend.order_item;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.seoyoon.backend.item.Item;
import org.seoyoon.backend.orders.Orders;

@Getter
@Setter
@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", nullable = false)
    @JsonBackReference
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private Integer quantity;

    private Long salePrice; // 주문 당시 판매가
    private Long costPrice; // 주문 당시 원가 (Item에서 복사)

    private Long profitAmount; // (salePrice - costPrice) * quantity
    private Integer profitRate; // (salePrice - costPrice) / salePrice * 10000

    @PrePersist
    public void calculateProfit() {
        if (salePrice != null && costPrice != null) {
            long profitPerUnit = salePrice - costPrice;
            this.profitAmount = profitPerUnit * quantity;
            this.profitRate = (int) Math.round(profitPerUnit * 10000.0 / salePrice);
        }
    }

    @Override
    public String toString() {
        return "OrderItem(" +
                "quantity=" + quantity +
                ", salePrice=" + salePrice +
                ", costPrice=" + costPrice +
                ")";
    }
}
