package com.webcodein.hikaricp.dto;

import java.math.BigDecimal;

public record OrderRequest(
        String customerName,
        String productCode,
        BigDecimal amount
) {
    public OrderRequest {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be null or blank");
        }
        if (productCode == null || productCode.isBlank()) {
            throw new IllegalArgumentException("Product code cannot be null or blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}
