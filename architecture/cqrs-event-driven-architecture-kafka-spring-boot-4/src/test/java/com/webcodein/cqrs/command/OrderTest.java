package com.webcodein.cqrs.command;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void shouldCreateOrderWithValidState() {
        Order order = new Order("CUST-100", 250.75, "CREATED");

        assertThat(order.getCustomerId()).isEqualTo("CUST-100");
        assertThat(order.getTotalAmount()).isEqualTo(250.75);
        assertThat(order.getStatus()).isEqualTo("CREATED");
    }

    @Test
    void shouldSupportNoArgsConstructorForJpa() {
        Order order = new Order();
        assertThat(order.getId()).isNull();
    }
}
