package com.webcodein.boundedcontext.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("Should initialize order in CREATED state with empty items")
    void shouldInitializeOrder() {
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getCustomerId()).isEqualTo(customerId);
        assertThat(order.getStatus()).isEqualTo("CREATED");
        assertThat(order.getItems()).isEmpty();
        assertThat(order.calculateTotal()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should add items and compute correct subtotal and order total")
    void shouldAddItemsAndComputeTotal() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(new OrderItem("SKU-1", "Product A", 25.0, 2));
        order.addItem(new OrderItem("SKU-2", "Product B", 10.0, 3));

        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getItems().get(0).getSubtotal()).isEqualTo(50.0);
        assertThat(order.getItems().get(1).getSubtotal()).isEqualTo(30.0);
        assertThat(order.calculateTotal()).isEqualTo(80.0);
    }

    @Test
    @DisplayName("Should confirm order when items are present")
    void shouldConfirmOrder() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(new OrderItem("SKU-1", "Product A", 25.0, 1));

        order.confirmOrder();
        assertThat(order.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("Should reject confirming order with zero items")
    void shouldRejectConfirmingEmptyOrder() {
        Order order = new Order(UUID.randomUUID());

        assertThatThrownBy(order::confirmOrder)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot confirm an order with zero items");
    }

    @Test
    @DisplayName("Should reject cancelling confirmed order")
    void shouldRejectCancellingConfirmedOrder() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(new OrderItem("SKU-1", "Product A", 25.0, 1));
        order.confirmOrder();

        assertThatThrownBy(order::cancelOrder)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot cancel an already confirmed order");
    }

    @Test
    @DisplayName("Should cancel unconfirmed order")
    void shouldCancelUnconfirmedOrder() {
        Order order = new Order(UUID.randomUUID());
        order.cancelOrder();

        assertThat(order.getStatus()).isEqualTo("CANCELLED");
    }
}
