package com.webcodein.architecture.flashsale.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    @DisplayName("Should create order with product and user identifiers")
    void shouldCreateOrder() {
        Order order = new Order("PROD-100", "USER-555");

        assertThat(order.getId()).isNull();
        assertThat(order.getProductId()).isEqualTo("PROD-100");
        assertThat(order.getUserId()).isEqualTo("USER-555");
    }

    @Test
    @DisplayName("Should support default constructor for JPA reflection")
    void shouldSupportDefaultConstructor() {
        Order order = new Order();
        assertThat(order.getId()).isNull();
        assertThat(order.getProductId()).isNull();
        assertThat(order.getUserId()).isNull();
    }
}
