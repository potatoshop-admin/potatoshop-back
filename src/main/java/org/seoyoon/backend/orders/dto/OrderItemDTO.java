package org.seoyoon.backend.orders.dto;

public record OrderItemDTO(
        Long orderItemId,
        Long itemId,
        String itemTitle,
        int quantity,
        Long salePrice,
        Long costPrice,
        Long profitAmount
) {}