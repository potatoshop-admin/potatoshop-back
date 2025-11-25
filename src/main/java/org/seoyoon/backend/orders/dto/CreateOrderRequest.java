package org.seoyoon.backend.orders.dto;

import java.util.List;

public record CreateOrderRequest(
        Long storeId,
        Long userId,
        String address,
        List<CreateOrderItemRequest> items
) {}