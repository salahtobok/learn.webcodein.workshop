package com.webcodein.workshop.cache.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductDto(
        @NotBlank(message = "Product ID must not be blank")
        String id,

        @NotBlank(message = "Name must not be blank")
        String name,

        @NotNull(message = "Price must not be null")
        @DecimalMin(value = "0.01", message = "Price must be strictly positive")
        BigDecimal price,

        int stockQuantity
) {
    public ProductDto {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be strictly positive");
        }
    }
}
