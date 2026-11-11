package com.webcodein.workshop.architecture.ddd.application;

import java.math.BigDecimal;

public record AddItemCommand(
        String bookId,
        int quantity,
        BigDecimal unitPrice,
        String currency
) {
}
