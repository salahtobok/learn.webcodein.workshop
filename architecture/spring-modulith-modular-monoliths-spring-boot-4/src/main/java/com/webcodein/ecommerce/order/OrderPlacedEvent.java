package com.webcodein.ecommerce.order;

public record OrderPlacedEvent(Long orderId, String productCode, int quantity) {}
