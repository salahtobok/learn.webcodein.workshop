package com.webcodein.workshop.ddd.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DDD Pattern: Order Aggregate Root Tests")
class OrderTest {

    @Test
    @DisplayName("Should create Order with CREATED status and valid attributes")
    void shouldCreateOrder() {
        Order order = new Order("customer@example.com", BigDecimal.valueOf(150.00));

        assertNull(order.getId());
        assertEquals("customer@example.com", order.getCustomerEmail().value());
        assertEquals(BigDecimal.valueOf(150.00), order.getTotalAmount().amount());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    @DisplayName("Should reject invalid email format")
    void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Order("bad-email", BigDecimal.valueOf(100.0)));
    }

    @Test
    @DisplayName("Should reject non-positive amount")
    void shouldRejectNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Order("customer@example.com", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new Order("customer@example.com", BigDecimal.valueOf(-10.0)));
    }

    @Test
    @DisplayName("Should fulfill CREATED order")
    void shouldFulfillCreatedOrder() {
        Order order = new Order("customer@example.com", BigDecimal.valueOf(100.0));
        order.fulfill();

        assertEquals(OrderStatus.FULFILLED, order.getStatus());
    }

    @Test
    @DisplayName("Should reject fulfilling already fulfilled order")
    void shouldRejectFulfillingAlreadyFulfilledOrder() {
        Order order = new Order("customer@example.com", BigDecimal.valueOf(100.0));
        order.fulfill();

        IllegalStateException ex = assertThrows(IllegalStateException.class, order::fulfill);
        assertTrue(ex.getMessage().contains("Only CREATED or PAID orders can be fulfilled"));
    }

    @Test
    @DisplayName("Should cancel order with reason")
    void shouldCancelOrderWithReason() {
        Order order = new Order("customer@example.com", BigDecimal.valueOf(100.0));
        order.cancel("Customer changed mind");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("Should reject cancelling already fulfilled order")
    void shouldRejectCancellingFulfilledOrder() {
        Order order = new Order("customer@example.com", BigDecimal.valueOf(100.0));
        order.fulfill();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            order.cancel("Too late");
        });
        assertTrue(ex.getMessage().contains("Cannot cancel an order that has already been fulfilled"));
    }

    @Test
    @DisplayName("Should reconstitute order from persistence without re-triggering validation")
    void shouldReconstituteOrder() {
        Order order = Order.reconstitute(
                42L,
                new Email("historic@example.com"),
                Money.usd(BigDecimal.valueOf(500.0)),
                OrderStatus.FULFILLED
        );

        assertEquals(42L, order.getId());
        assertEquals("historic@example.com", order.getCustomerEmail().value());
        assertEquals(BigDecimal.valueOf(500.0), order.getTotalAmount().amount());
        assertEquals(OrderStatus.FULFILLED, order.getStatus());
    }
}
