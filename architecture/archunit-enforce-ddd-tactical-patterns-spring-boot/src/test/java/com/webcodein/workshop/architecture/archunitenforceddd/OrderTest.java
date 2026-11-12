package com.webcodein.workshop.architecture.archunitenforceddd;

import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Money;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Order;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.OrderLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Aggregate Root Invariant Tests")
class OrderTest {

    @Test
    @DisplayName("Should create order with initial CREATED status")
    void shouldCreateOrderWithCreatedStatus() {
        UUID id = UUID.randomUUID();
        Order order = new Order(id, "CUST-100");

        assertEquals(id, order.getId());
        assertEquals("CUST-100", order.getCustomerId());
        assertEquals(Order.OrderStatus.CREATED, order.getStatus());
        assertTrue(order.getLines().isEmpty());
    }

    @Test
    @DisplayName("Should add line to order when in CREATED status")
    void shouldAddLineToOrder() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");
        OrderLine line = new OrderLine(UUID.randomUUID(), "PROD-1", new Money(new BigDecimal("49.99"), "USD"));

        order.addLine(line);

        assertEquals(1, order.getLines().size());
        assertEquals(line, order.getLines().get(0));
    }

    @Test
    @DisplayName("Should transition to PAID status when order has lines")
    void shouldTransitionToPaid() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");
        order.addLine(new OrderLine(UUID.randomUUID(), "PROD-1", new Money(new BigDecimal("49.99"), "USD")));

        order.pay();

        assertEquals(Order.OrderStatus.PAID, order.getStatus());
    }

    @Test
    @DisplayName("Should reject payment when order has no lines")
    void shouldRejectPaymentWhenEmpty() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");

        IllegalStateException ex = assertThrows(IllegalStateException.class, order::pay);
        assertEquals("Cannot pay for an order with no lines", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject line additions after payment")
    void shouldRejectLineAdditionAfterPayment() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");
        order.addLine(new OrderLine(UUID.randomUUID(), "PROD-1", new Money(new BigDecimal("49.99"), "USD")));
        order.pay();

        assertThrows(IllegalStateException.class, () ->
                order.addLine(new OrderLine(UUID.randomUUID(), "PROD-2", new Money(new BigDecimal("10.00"), "USD"))));
    }

    @Test
    @DisplayName("Should cancel order successfully")
    void shouldCancelOrder() {
        Order order = new Order(UUID.randomUUID(), "CUST-100");
        order.cancel();

        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
    }
}
