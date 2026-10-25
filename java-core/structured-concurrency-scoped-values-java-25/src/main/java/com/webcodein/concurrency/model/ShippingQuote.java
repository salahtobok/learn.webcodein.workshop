package com.webcodein.concurrency.model;

import java.math.BigDecimal;
import java.time.Instant;

public record ShippingQuote(
        String carrierName,
        BigDecimal estimatedCost,
        int transitDays,
        Instant quoteTimestamp,
        String correlationId
) {
}
