package com.webcodein.workshop.ai.agent;

import com.webcodein.workshop.ai.agent.tool.OrderTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderToolsTest {

    private OrderTools orderTools;

    @BeforeEach
    void setUp() {
        orderTools = new OrderTools();
    }

    @Test
    @DisplayName("Should return FOUND status for an existing order")
    void shouldReturnFoundForExistingOrder() {
        OrderTools.OrderStatusResponse response = orderTools.getOrderStatus("ORD-1001");
        assertThat(response.orderId()).isEqualTo("ORD-1001");
        assertThat(response.status()).isEqualTo("FOUND");
        assertThat(response.message()).contains("Shipped");
    }

    @Test
    @DisplayName("Should return NOT_FOUND status for non-existent order")
    void shouldReturnNotFoundForMissingOrder() {
        OrderTools.OrderStatusResponse response = orderTools.getOrderStatus("ORD-9999");
        assertThat(response.orderId()).isEqualTo("ORD-9999");
        assertThat(response.status()).isEqualTo("NOT_FOUND");
    }

    @Test
    @DisplayName("Should cancel an order that is currently in Processing state")
    void shouldCancelProcessingOrder() {
        OrderTools.CancelOrderResponse response = orderTools.cancelOrder("ORD-1002", "Customer requested cancellation");
        assertThat(response.success()).isTrue();
        assertThat(response.message()).contains("successfully cancelled");

        // Verify it was removed
        OrderTools.OrderStatusResponse statusCheck = orderTools.getOrderStatus("ORD-1002");
        assertThat(statusCheck.status()).isEqualTo("NOT_FOUND");
    }

    @Test
    @DisplayName("Should reject cancellation for an already shipped order")
    void shouldRejectCancellationForShippedOrder() {
        OrderTools.CancelOrderResponse response = orderTools.cancelOrder("ORD-1001", "Changed my mind");
        assertThat(response.success()).isFalse();
        assertThat(response.message()).contains("already been shipped");
    }

    @Test
    @DisplayName("Should reject cancellation for an order that does not exist")
    void shouldRejectCancellationForMissingOrder() {
        OrderTools.CancelOrderResponse response = orderTools.cancelOrder("ORD-9999", "Unknown order");
        assertThat(response.success()).isFalse();
        assertThat(response.message()).contains("Order not found");
    }
}
