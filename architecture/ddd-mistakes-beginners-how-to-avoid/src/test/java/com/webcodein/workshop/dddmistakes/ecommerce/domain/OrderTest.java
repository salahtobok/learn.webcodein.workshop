package com.webcodein.workshop.dddmistakes.ecommerce.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Aggregate Lifecycle & Invariant Tests")
class OrderTest {

    @Test
    @DisplayName("Should create order successfully with CREATED status and zero total")
    void shouldCreateOrderSuccessfully() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, "CUST-100");

        assertEquals(orderId, order.getId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should reject order creation with null ID or blank customer")
    void shouldRejectOrderWithNullOrBlankCustomer() {
        assertThrows(IllegalArgumentException.class, () -> new Order(null, "CUST-100"));
        assertThrows(IllegalArgumentException.class, () -> new Order(UUID.randomUUID(), ""));
        assertThrows(IllegalArgumentException.class, () -> new Order(UUID.randomUUID(), "   "));
    }

    @Test
    @DisplayName("Should add items and recalculate total amount correctly")
    void shouldAddItemsAndRecalculateTotal() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");
        Product p1 = new Product(UUID.randomUUID(), new BigDecimal("29.99"));
        Product p2 = new Product(UUID.randomUUID(), new BigDecimal("15.50"));

        order.addItem(p1, 2);
        order.addItem(p2, 1);

        assertEquals(2, order.getItems().size());
        assertEquals(new BigDecimal("75.48"), order.getTotalAmount());
    }

    @Test
    @DisplayName("Should reject adding item with zero or negative quantity")
    void shouldRejectInvalidQuantity() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");
        Product product = new Product(UUID.randomUUID(), new BigDecimal("50.00"));

        assertThrows(IllegalArgumentException.class, () -> order.addItem(product, 0));
        assertThrows(IllegalArgumentException.class, () -> order.addItem(product, -3));
    }

    @Test
    @DisplayName("Should confirm order with items successfully")
    void shouldConfirmOrderWithItems() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");
        Product product = new Product(UUID.randomUUID(), new BigDecimal("49.99"));
        order.addItem(product, 1);

        order.confirm();
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @DisplayName("Should reject confirming an empty order")
    void shouldRejectConfirmingEmptyOrder() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");

        IllegalStateException ex = assertThrows(IllegalStateException.class, order::confirm);
        assertEquals("Cannot confirm an order with no items", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject adding items after order is confirmed")
    void shouldRejectAddingItemsToConfirmedOrder() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");
        Product product = new Product(UUID.randomUUID(), new BigDecimal("10.00"));
        order.addItem(product, 1);
        order.confirm();

        assertThrows(IllegalStateException.class, () -> order.addItem(product, 1));
    }

    @Test
    @DisplayName("Should cancel created order successfully")
    void shouldCancelCreatedOrder() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");
        order.cancel();

        assertEquals(OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    @DisplayName("Should reject canceling shipped order")
    void cannotCancelShippedOrder() {
        Order order = new Order(UUID.randomUUID(), "CUST-1");
        order.markAsShipped();

        IllegalStateException exception = assertThrows(IllegalStateException.class, order::cancel);
        assertEquals("Order has already shipped and cannot be canceled", exception.getMessage());
    }
}
