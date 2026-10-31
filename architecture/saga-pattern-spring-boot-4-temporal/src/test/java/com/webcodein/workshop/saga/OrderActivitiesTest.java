package com.webcodein.workshop.saga;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderActivitiesTest {

    private OrderActivitiesImpl activities;

    @BeforeEach
    void setUp() {
        activities = new OrderActivitiesImpl();
    }

    @Test
    @DisplayName("processPayment should succeed for valid account and amount")
    void testProcessPayment() {
        assertThatCode(() -> activities.processPayment("cust-100", 250.0))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("refundPayment should succeed as compensating transaction")
    void testRefundPayment() {
        assertThatCode(() -> activities.refundPayment("cust-100", 250.0))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("reserveInventory should succeed for valid order without failure keyword")
    void testReserveInventorySuccess() {
        assertThatCode(() -> activities.reserveInventory("order-valid-999"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("reserveInventory should throw RuntimeException when orderId contains fail")
    void testReserveInventoryFailure() {
        assertThatThrownBy(() -> activities.reserveInventory("order-fail-item"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Out of stock!");
    }

    @Test
    @DisplayName("releaseInventory should succeed as compensating transaction")
    void testReleaseInventory() {
        assertThatCode(() -> activities.releaseInventory("order-valid-999"))
                .doesNotThrowAnyException();
    }
}
