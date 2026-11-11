package com.webcodein.ddd.domain.model.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("New order should have PENDING status and empty items list")
    void newOrderIsPending() {
        Order order = new Order(UUID.randomUUID());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertTrue(order.getItems().isEmpty());
        assertEquals(0.0, order.getTotalAmount());
    }

    @Test
    @DisplayName("Creating order with null customer ID should throw IllegalArgumentException")
    void nullCustomerIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Order(null));
    }

    @Test
    @DisplayName("Should successfully add item to pending order")
    void canAddItemToPendingOrder() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(UUID.randomUUID(), 2, 10.5);
        assertEquals(1, order.getItems().size());
        assertEquals(21.0, order.getTotalAmount());
    }

    @Test
    @DisplayName("Cannot confirm empty order")
    void cannotConfirmEmptyOrder() {
        Order order = new Order(UUID.randomUUID());
        IllegalStateException ex = assertThrows(IllegalStateException.class, order::confirm);
        assertTrue(ex.getMessage().contains("Cannot confirm an empty order"));
    }

    @Test
    @DisplayName("Can confirm order that has items")
    void canConfirmOrderWithItems() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(UUID.randomUUID(), 2, 10.5);
        order.confirm();
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @DisplayName("Cannot add item to confirmed order")
    void cannotAddItemToConfirmedOrder() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(UUID.randomUUID(), 2, 10.5);
        order.confirm();
        assertThrows(IllegalStateException.class, () -> order.addItem(UUID.randomUUID(), 1, 5.0));
    }

    @Test
    @DisplayName("Cannot re-confirm an already confirmed order")
    void cannotReconfirmOrder() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(UUID.randomUUID(), 1, 15.0);
        order.confirm();
        assertThrows(IllegalStateException.class, order::confirm);
    }

    @Test
    @DisplayName("Can cancel pending and confirmed orders")
    void canCancelOrder() {
        Order order1 = new Order(UUID.randomUUID());
        order1.cancel();
        assertEquals(OrderStatus.CANCELLED, order1.getStatus());

        Order order2 = new Order(UUID.randomUUID());
        order2.addItem(UUID.randomUUID(), 1, 50.0);
        order2.confirm();
        order2.cancel();
        assertEquals(OrderStatus.CANCELLED, order2.getStatus());
    }

    @Test
    @DisplayName("Cannot cancel an already cancelled order")
    void cannotCancelAlreadyCancelledOrder() {
        Order order = new Order(UUID.randomUUID());
        order.cancel();
        assertThrows(IllegalStateException.class, order::cancel);
    }

    @Test
    @DisplayName("Calculates total order amount across multiple items")
    void calculatesTotalAmountAcrossItems() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(UUID.randomUUID(), 3, 10.0); // 30.0
        order.addItem(UUID.randomUUID(), 2, 25.5); // 51.0
        assertEquals(81.0, order.getTotalAmount());
    }

    @Test
    @DisplayName("Order items list must be unmodifiable to protect aggregate invariants")
    void orderItemsListIsUnmodifiable() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(UUID.randomUUID(), 1, 10.0);
        assertThrows(UnsupportedOperationException.class, () -> 
                order.getItems().add(new OrderItem(UUID.randomUUID(), 1, 20.0)));
    }
}
