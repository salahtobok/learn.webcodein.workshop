package com.webcodein.boundedcontext.order.dto;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
    UUID customerId,
    List<OrderItemRequest> items
) {}
