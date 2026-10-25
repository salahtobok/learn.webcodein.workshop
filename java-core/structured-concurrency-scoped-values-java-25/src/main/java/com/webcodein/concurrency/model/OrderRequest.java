package com.webcodein.concurrency.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank(message = "Order ID is required")
        String orderId,

        @NotBlank(message = "Customer ID is required")
        String customerId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @Min(value = 1, message = "Item count must be at least 1")
        int itemCount
) {
}
