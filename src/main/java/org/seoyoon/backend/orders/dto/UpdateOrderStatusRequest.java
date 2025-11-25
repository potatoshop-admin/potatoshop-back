package org.seoyoon.backend.orders.dto;

import org.seoyoon.backend.orders.OrderStatusType;

public record UpdateOrderStatusRequest(
        OrderStatusType orderStatus
) {}