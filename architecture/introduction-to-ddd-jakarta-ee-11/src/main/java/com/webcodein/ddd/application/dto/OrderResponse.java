package com.webcodein.ddd.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String status,
        BigDecimal totalAmount,
        AddressDto shippingAddress,
        List<OrderItemDto> items
) {
}
