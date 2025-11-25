package org.seoyoon.backend.orders;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.seoyoon.backend.order_item.OrderItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Orders {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ordersId;
    private Long storeId;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderStatusType orderStatus;

    private Integer totalPrice;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    private String address;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
