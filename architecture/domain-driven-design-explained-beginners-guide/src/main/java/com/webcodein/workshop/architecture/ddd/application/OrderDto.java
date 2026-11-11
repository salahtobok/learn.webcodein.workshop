package com.webcodein.workshop.architecture.ddd.application;

import com.webcodein.workshop.architecture.ddd.domain.model.order.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record OrderDto(
        UUID id,
        String customerEmail,
        String status,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt,
        List<OrderLineDto> lines
) {
    public static OrderDto fromDomain(Order order) {
        List<OrderLineDto> lineDtos = order.getLines().stream()
                .map(OrderLineDto::fromDomain)
                .collect(Collectors.toList());

        return new OrderDto(
                order.getId().value(),
                order.getCustomerEmail().value(),
                order.getStatus().name(),
                order.totalAmount().amount(),
                order.totalAmount().currency(),
                order.getCreatedAt(),
                lineDtos
        );
    }
}
