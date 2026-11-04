package com.webcodein.boundedcontext.order.dto;

public record OrderItemRequest(
    String sku,
    int quantity
) {}
