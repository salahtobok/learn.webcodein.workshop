package com.webcodein.workshop.kafka.unit;

import com.webcodein.workshop.kafka.model.OrderEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderEventTest {

    @Test
    @DisplayName("Should create valid OrderEvent")
    void shouldCreateValidOrderEvent() {
        OrderEvent event = new OrderEvent(
                "ORD-101",
                "CUST-500",
                new BigDecimal("99.95"),
                "ORDER_CREATED",
                0
        );

        assertThat(event.orderId()).isEqualTo("ORD-101");
        assertThat(event.customerId()).isEqualTo("CUST-500");
        assertThat(event.amount()).isEqualByComparingTo(new BigDecimal("99.95"));
        assertThat(event.eventType()).isEqualTo("ORDER_CREATED");
        assertThat(event.retryCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should increment retry count cleanly")
    void shouldIncrementRetryCount() {
        OrderEvent event = new OrderEvent("ORD-101", "CUST-500", new BigDecimal("50.00"), "ORDER_CREATED", 1);
        OrderEvent next = event.withIncrementedRetry();

        assertThat(next.retryCount()).isEqualTo(2);
        assertThat(next.orderId()).isEqualTo(event.orderId());
    }

    @Test
    @DisplayName("Should throw exception when amount is zero or negative")
    void shouldThrowWhenAmountIsNonPositive() {
        assertThatThrownBy(() -> new OrderEvent("ORD-1", "CUST-1", BigDecimal.ZERO, "CREATED", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("amount must be positive");
    }

    @Test
    @DisplayName("Should throw exception when orderId is blank")
    void shouldThrowWhenOrderIdIsBlank() {
        assertThatThrownBy(() -> new OrderEvent("   ", "CUST-1", BigDecimal.TEN, "CREATED", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("orderId must not be blank");
    }
}
