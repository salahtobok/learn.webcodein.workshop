package com.webcodein.workshop.architecture.hexagonal.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderDomainTest {

    @Test
    @DisplayName("Should create valid order when parameters satisfy domain invariants")
    void shouldCreateValidOrder() {
        UUID id = UUID.randomUUID();
        Order order = new Order(id, "Mechanical Keyboard", 2);

        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getProduct()).isEqualTo("Mechanical Keyboard");
        assertThat(order.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should reject order with null ID")
    void shouldRejectNullId() {
        assertThatThrownBy(() -> new Order(null, "Laptop", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order ID cannot be null");
    }

    @Test
    @DisplayName("Should reject order with null or blank product name")
    void shouldRejectBlankProduct() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> new Order(id, "", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product name cannot be null or blank");

        assertThatThrownBy(() -> new Order(id, "   ", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product name cannot be null or blank");
    }

    @Test
    @DisplayName("Should reject order with non-positive quantity")
    void shouldRejectNonPositiveQuantity() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> new Order(id, "Monitor", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");

        assertThatThrownBy(() -> new Order(id, "Monitor", -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    @DisplayName("Should satisfy equality and hash code based on identity")
    void shouldSatisfyEqualityBasedOnIdentity() {
        UUID id = UUID.randomUUID();
        Order order1 = new Order(id, "Mouse", 1);
        Order order2 = new Order(id, "Mouse", 1);

        assertThat(order1).isEqualTo(order2);
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
    }
}
