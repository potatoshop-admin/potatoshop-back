package org.seoyoon.backend.orders.dto;

public record CreateOrderItemRequest(
        Long itemId,
        Integer quantity
) {}
