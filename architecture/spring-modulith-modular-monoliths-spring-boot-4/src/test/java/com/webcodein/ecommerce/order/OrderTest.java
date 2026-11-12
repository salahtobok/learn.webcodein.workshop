package com.webcodein.ecommerce.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Entity Invariant & State Transition Tests")
class OrderTest {

    @Test
    @DisplayName("Should initialize order in PENDING status with valid fields")
    void shouldCreateValidOrder() {
        Order order = new Order("LAPTOP-99", 3);
        assertEquals("LAPTOP-99", order.getProductCode());
        assertEquals(3, order.getQuantity());
        assertEquals("PENDING", order.getStatus());
    }

    @Test
    @DisplayName("Should transition from PENDING to COMPLETED")
    void shouldCompleteOrder() {
        Order order = new Order("KEYBOARD-RGB", 1);
        order.complete();
        assertEquals("COMPLETED", order.getStatus());
    }

    @Test
    @DisplayName("Should transition from PENDING to CANCELLED")
    void shouldCancelOrder() {
        Order order = new Order("MOUSE-WIRELESS", 2);
        order.cancel();
        assertEquals("CANCELLED", order.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when attempting to complete a cancelled order")
    void shouldThrowWhenCompletingCancelledOrder() {
        Order order = new Order("MONITOR-4K", 1);
        order.cancel();
        assertThrows(IllegalStateException.class, order::complete);
    }

    @Test
    @DisplayName("Should throw exception when productCode is blank or quantity non-positive")
    void shouldThrowOnInvalidConstruction() {
        assertThrows(IllegalArgumentException.class, () -> new Order("", 5));
        assertThrows(IllegalArgumentException.class, () -> new Order("PHONE", 0));
        assertThrows(IllegalArgumentException.class, () -> new Order("PHONE", -2));
    }
}
