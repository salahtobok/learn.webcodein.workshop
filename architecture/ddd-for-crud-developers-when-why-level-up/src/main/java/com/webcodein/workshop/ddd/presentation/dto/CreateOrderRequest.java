package com.webcodein.workshop.ddd.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID customerId,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotBlank String productCode,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
        @Min(1) int quantity
) {}
