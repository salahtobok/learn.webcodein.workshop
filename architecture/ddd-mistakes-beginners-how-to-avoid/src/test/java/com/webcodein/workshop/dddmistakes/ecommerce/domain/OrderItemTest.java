package com.webcodein.workshop.dddmistakes.ecommerce.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItem Unit Tests")
class OrderItemTest {

    @Test
    @DisplayName("Should create OrderItem and compute subtotal correctly")
    void shouldComputeSubtotalCorrectly() {
        UUID productId = UUID.randomUUID();
        BigDecimal price = new BigDecimal("19.99");
        OrderItem item = new OrderItem(productId, price, 3);

        assertEquals(new BigDecimal("59.97"), item.getSubtotal());
    }
}
