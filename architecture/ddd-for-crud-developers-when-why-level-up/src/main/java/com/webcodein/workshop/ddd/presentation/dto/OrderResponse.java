package com.webcodein.workshop.ddd.presentation.dto;

import com.webcodein.workshop.ddd.domain.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        UUID customerId,
        String status,
        String currency,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal totalAmount,
        List<ItemDto> items
) {
    public record ItemDto(UUID itemId, String productCode, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {}

    public static OrderResponse from(Order order) {
        List<ItemDto> itemDtos = order.getItems().stream()
                .map(item -> new ItemDto(
                        item.itemId(),
                        item.productCode(),
                        item.unitPrice().amount(),
                        item.quantity(),
                        item.lineTotal().amount()
                ))
                .toList();

        return new OrderResponse(
                order.getId().value(),
                order.getCustomerId().value(),
                order.getStatus().name(),
                order.getCurrency(),
                order.calculateSubtotal().amount(),
                order.getDiscount().amount(),
                order.calculateTotal().amount(),
                itemDtos
        );
    }
}
