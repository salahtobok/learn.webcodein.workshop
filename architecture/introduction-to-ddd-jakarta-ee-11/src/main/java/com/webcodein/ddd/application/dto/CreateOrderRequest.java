package com.webcodein.ddd.application.dto;

import java.util.List;

public record CreateOrderRequest(
        AddressDto shippingAddress,
        List<OrderItemDto> items
) {
}
