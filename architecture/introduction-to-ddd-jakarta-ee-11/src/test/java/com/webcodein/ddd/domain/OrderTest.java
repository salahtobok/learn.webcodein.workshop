package com.webcodein.ddd.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private final Address testAddress = new Address("123 Main St", "Tech City", "10001");

    @Test
    @DisplayName("Should initialize order in PENDING status with generated UUID")
    void shouldInitializeInPendingStatus() {
        Order order = new Order(testAddress);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo("PENDING");
        assertThat(order.getShippingAddress()).isEqualTo(testAddress);
        assertThat(order.getItems()).isEmpty();
        assertThat(order.calculateTotal()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should add items and compute aggregate total in PENDING status")
    void shouldAddItemsAndComputeTotal() {
        Order order = new Order(testAddress);
        order.addItem(new OrderItem("Monitor", 1, new BigDecimal("300.00")));
        order.addItem(new OrderItem("Mousepad", 2, new BigDecimal("15.50")));

        assertThat(order.getItems()).hasSize(2);
        assertThat(order.calculateTotal()).isEqualTo(new BigDecimal("331.00"));
    }

    @Test
    @DisplayName("Should forbid confirming an order with no items")
    void shouldForbidConfirmingEmptyOrder() {
        Order order = new Order(testAddress);

        assertThatThrownBy(order::confirm)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot confirm an order with no items");
    }

    @Test
    @DisplayName("Should transition from PENDING to CONFIRMED and SHIPPED")
    void shouldTransitionLifecycleStates() {
        Order order = new Order(testAddress);
        order.addItem(new OrderItem("Headset", 1, new BigDecimal("99.00")));

        order.confirm();
        assertThat(order.getStatus()).isEqualTo("CONFIRMED");

        // Cannot add items once confirmed
        assertThatThrownBy(() -> order.addItem(new OrderItem("Extra", 1, BigDecimal.TEN)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot add items to an order that is not PENDING");

        order.ship();
        assertThat(order.getStatus()).isEqualTo("SHIPPED");
    }

    @Test
    @DisplayName("Should forbid shipping an order that is not CONFIRMED")
    void shouldForbidShippingUnconfirmedOrder() {
        Order order = new Order(testAddress);
        order.addItem(new OrderItem("Phone", 1, new BigDecimal("800.00")));

        assertThatThrownBy(order::ship)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Order must be CONFIRMED before it can be SHIPPED");
    }

    @Test
    @DisplayName("Items collection should be unmodifiable from outside")
    void shouldEncapsulateItemsCollection() {
        Order order = new Order(testAddress);
        order.addItem(new OrderItem("Laptop", 1, new BigDecimal("1200.00")));

        assertThatThrownBy(() -> order.getItems().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
