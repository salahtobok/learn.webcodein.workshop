package com.webcodein.ddd.application.dto;

import java.math.BigDecimal;

public record OrderItemDto(String productName, int quantity, BigDecimal price) {
}
