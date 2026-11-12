package com.webcodein.ddd.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    @Test
    @DisplayName("Should create valid order item and compute total price")
    void shouldCreateValidItemAndComputeTotal() {
        OrderItem item = new OrderItem("Mechanical Keyboard", 2, new BigDecimal("120.00"));

        assertThat(item.getId()).isNotNull();
        assertThat(item.getProductName()).isEqualTo("Mechanical Keyboard");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getPrice()).isEqualTo(new BigDecimal("120.00"));
        assertThat(item.getTotalPrice()).isEqualTo(new BigDecimal("240.00"));
    }

    @Test
    @DisplayName("Should reject non-positive quantity")
    void shouldRejectNonPositiveQuantity() {
        assertThatThrownBy(() -> new OrderItem("USB Cable", 0, new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");

        assertThatThrownBy(() -> new OrderItem("USB Cable", -1, new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    @DisplayName("Should reject negative unit price")
    void shouldRejectNegativePrice() {
        assertThatThrownBy(() -> new OrderItem("USB Cable", 1, new BigDecimal("-5.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price cannot be negative");
    }
}
