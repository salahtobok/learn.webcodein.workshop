package com.webcodein.boundedcontext.order.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
    UUID id,
    UUID customerId,
    String status,
    Instant createdAt,
    double totalAmount,
    List<OrderItemResponseDto> items
) {}
