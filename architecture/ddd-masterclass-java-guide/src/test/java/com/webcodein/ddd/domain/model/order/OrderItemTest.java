package com.webcodein.ddd.domain.model.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    @DisplayName("Valid order item should calculate subtotal correctly")
    void validOrderItemCalculatesSubtotal() {
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 3, 19.99);

        assertEquals(productId, item.productId());
        assertEquals(3, item.quantity());
        assertEquals(19.99, item.price());
        assertEquals(59.97, item.subtotal(), 0.001);
    }

    @Test
    @DisplayName("Order item with null product ID should throw IllegalArgumentException")
    void nullProductIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(null, 1, 10.0));
    }

    @Test
    @DisplayName("Order item with zero or negative quantity should throw IllegalArgumentException")
    void invalidQuantityThrowsException() {
        UUID productId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(productId, 0, 10.0));
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(productId, -2, 10.0));
    }

    @Test
    @DisplayName("Order item with negative price should throw IllegalArgumentException")
    void negativePriceThrowsException() {
        UUID productId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(productId, 1, -5.0));
    }

    @Test
    @DisplayName("Order item records should follow value object equality semantics")
    void valueObjectEquality() {
        UUID productId = UUID.randomUUID();
        OrderItem item1 = new OrderItem(productId, 2, 15.0);
        OrderItem item2 = new OrderItem(productId, 2, 15.0);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }
}
