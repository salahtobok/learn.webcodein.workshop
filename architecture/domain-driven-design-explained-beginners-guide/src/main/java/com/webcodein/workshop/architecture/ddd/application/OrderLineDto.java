package com.webcodein.workshop.architecture.ddd.application;

import com.webcodein.workshop.architecture.ddd.domain.model.order.OrderLine;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderLineDto(
        UUID id,
        String bookId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        String currency
) {
    public static OrderLineDto fromDomain(OrderLine line) {
        return new OrderLineDto(
                line.getId(),
                line.getBookId().value(),
                line.getQuantity().value(),
                line.getUnitPrice().amount(),
                line.subtotal().amount(),
                line.getUnitPrice().currency()
        );
    }
}
