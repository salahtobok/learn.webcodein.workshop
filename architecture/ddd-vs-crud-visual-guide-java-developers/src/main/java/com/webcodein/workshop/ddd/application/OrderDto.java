package com.webcodein.workshop.ddd.application;

import com.webcodein.workshop.ddd.domain.Order;

import java.math.BigDecimal;

public record OrderDto(
        Long id,
        String customerEmail,
        BigDecimal totalAmount,
        String currency,
        String status
) {
    public static OrderDto fromDomain(Order order) {
        return new OrderDto(
                order.getId(),
                order.getCustomerEmail().value(),
                order.getTotalAmount().amount(),
                order.getTotalAmount().currency(),
                order.getStatus().name()
        );
    }
}
