package com.webcodein.workshop.outbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {

    @Test
    @DisplayName("Should initialize OutboxEvent with UUID and current timestamp")
    void shouldInitializeOutboxEvent() {
        Instant before = Instant.now().minusSeconds(1);
        OutboxEvent event = new OutboxEvent(
                "Order",
                "101",
                "OrderCreated",
                "{\"product\":\"iPad Pro\",\"price\":999.00}"
        );

        assertThat(event.getId()).isNotNull();
        assertThat(event.getAggregateType()).isEqualTo("Order");
        assertThat(event.getAggregateId()).isEqualTo("101");
        assertThat(event.getEventType()).isEqualTo("OrderCreated");
        assertThat(event.getPayload()).contains("iPad Pro");
        assertThat(event.getCreatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    @DisplayName("Default constructor should instantiate empty outbox event")
    void defaultConstructor() {
        OutboxEvent event = new OutboxEvent();
        assertThat(event.getId()).isNull();
        assertThat(event.getAggregateType()).isNull();
        assertThat(event.getAggregateId()).isNull();
        assertThat(event.getEventType()).isNull();
        assertThat(event.getPayload()).isNull();
        assertThat(event.getCreatedAt()).isNull();
    }
}
