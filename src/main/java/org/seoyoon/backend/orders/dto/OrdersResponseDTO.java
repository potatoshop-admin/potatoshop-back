package org.seoyoon.backend.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrdersResponseDTO(
        Long ordersId,
        UserInfoDTO user,
        Long storeId,
        String address,
        String orderStatus,
        int totalPrice,
        LocalDateTime createdAt,
        List<OrderItemDTO> orderItems
) {}