package com.webcodein.cqrs.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCreatedEventTest {

    @Test
    void shouldCreateEventRecord() {
        OrderCreatedEvent event = new OrderCreatedEvent(1L, "CUST-300", 1500.00);

        assertThat(event.orderId()).isEqualTo(1L);
        assertThat(event.customerId()).isEqualTo("CUST-300");
        assertThat(event.totalAmount()).isEqualTo(1500.00);
    }

    @Test
    void shouldSupportEqualityAndHashCode() {
        OrderCreatedEvent e1 = new OrderCreatedEvent(1L, "CUST-300", 1500.00);
        OrderCreatedEvent e2 = new OrderCreatedEvent(1L, "CUST-300", 1500.00);

        assertThat(e1).isEqualTo(e2);
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
        assertThat(e1.toString()).contains("1500.0");
    }
}
