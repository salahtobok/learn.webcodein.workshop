package com.webcodein.ecommerce.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    @Test
    @DisplayName("Should create OrderItem with valid fields")
    void shouldCreateOrderItem() {
        Money price = Money.of(new BigDecimal("19.99"), "EUR");
        OrderItem item = new OrderItem("PROD-88", price, 3);

        assertThat(item.getProductId()).isEqualTo("PROD-88");
        assertThat(item.getPrice()).isEqualTo(price);
        assertThat(item.getQuantity()).isEqualTo(3);
        assertThat(item.getId()).isNull();
    }

    @Test
    @DisplayName("Should throw when quantity is zero or negative")
    void shouldThrowWhenQuantityInvalid() {
        Money price = Money.of(new BigDecimal("19.99"), "EUR");

        assertThatThrownBy(() -> new OrderItem("PROD-88", price, 0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new OrderItem("PROD-88", price, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw when productId or price is null")
    void shouldThrowWhenNull() {
        Money price = Money.of(new BigDecimal("19.99"), "EUR");

        assertThatThrownBy(() -> new OrderItem(null, price, 1))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new OrderItem("PROD-1", null, 1))
                .isInstanceOf(NullPointerException.class);
    }
}
