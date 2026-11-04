package com.webcodein.boundedcontext.order.dto;

import java.util.UUID;

public record OrderItemResponseDto(
    UUID id,
    String sku,
    String productName,
    double unitPrice,
    int quantity,
    double subtotal
) {}
