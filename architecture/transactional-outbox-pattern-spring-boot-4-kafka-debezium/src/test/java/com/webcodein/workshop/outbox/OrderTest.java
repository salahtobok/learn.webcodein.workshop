package com.webcodein.workshop.outbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    @DisplayName("Should create order with product and price")
    void shouldCreateOrder() {
        Order order = new Order("MacBook Pro", new BigDecimal("2499.99"));

        assertThat(order.getProduct()).isEqualTo("MacBook Pro");
        assertThat(order.getPrice()).isEqualTo(new BigDecimal("2499.99"));
        assertThat(order.getId()).isNull();
    }

    @Test
    @DisplayName("Default constructor should instantiate empty order")
    void defaultConstructor() {
        Order order = new Order();
        assertThat(order.getId()).isNull();
        assertThat(order.getProduct()).isNull();
        assertThat(order.getPrice()).isNull();
    }
}
