package com.webcodein.workshop.architecture.outbox.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
    UUID orderId,
    String customerId,
    String productCode,
    int quantity,
    BigDecimal totalAmount,
    Instant timestamp
) {}
