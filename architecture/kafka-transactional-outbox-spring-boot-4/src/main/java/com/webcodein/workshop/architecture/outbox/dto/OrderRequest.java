package com.webcodein.workshop.architecture.outbox.dto;

import java.math.BigDecimal;

public record OrderRequest(
    String customerId,
    String productCode,
    int quantity,
    BigDecimal totalAmount
) {}
