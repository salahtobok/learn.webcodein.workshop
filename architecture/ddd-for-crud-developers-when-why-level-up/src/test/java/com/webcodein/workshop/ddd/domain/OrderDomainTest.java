package com.webcodein.workshop.ddd.domain;

import com.webcodein.workshop.ddd.domain.model.CustomerId;
import com.webcodein.workshop.ddd.domain.model.Money;
import com.webcodein.workshop.ddd.domain.model.Order;
import com.webcodein.workshop.ddd.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderDomainTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.create(CustomerId.generate(), "USD");
    }

    @Test
    @DisplayName("Should initialize order in CREATED status with zero balance")
    void initialOrderState() {
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals("USD", order.getCurrency());
        assertEquals(Money.zero("USD"), order.calculateTotal());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should add items and compute subtotal correctly")
    void addItemsComputesSubtotal() {
        order.addItem("PROD-1", Money.of(10.00, "USD"), 2);
        order.addItem("PROD-2", Money.of(15.50, "USD"), 1);

        assertEquals(2, order.getItems().size());
        assertEquals(Money.of(35.50, "USD"), order.calculateSubtotal());
        assertEquals(Money.of(35.50, "USD"), order.calculateTotal());
    }

    @Test
    @DisplayName("Should reject adding item with mismatched currency")
    void rejectCurrencyMismatchItem() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                order.addItem("PROD-FOREIGN", Money.of(10.00, "EUR"), 1)
        );
        assertTrue(ex.getMessage().contains("does not match order currency"));
    }

    @Test
    @DisplayName("Should apply valid discount and reduce total")
    void applyValidDiscount() {
        order.addItem("PROD-1", Money.of(50.00, "USD"), 1);
        order.applyDiscount(Money.of(10.00, "USD"));

        assertEquals(Money.of(50.00, "USD"), order.calculateSubtotal());
        assertEquals(Money.of(10.00, "USD"), order.getDiscount());
        assertEquals(Money.of(40.00, "USD"), order.calculateTotal());
    }

    @Test
    @DisplayName("Should reject discount exceeding subtotal")
    void rejectDiscountExceedingSubtotal() {
        order.addItem("PROD-1", Money.of(20.00, "USD"), 1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                order.applyDiscount(Money.of(25.00, "USD"))
        );
        assertTrue(ex.getMessage().contains("exceeds order subtotal"));
    }

    @Test
    @DisplayName("Should confirm order with items and allow shipping")
    void confirmAndShipOrder() {
        order.addItem("PROD-1", Money.of(30.00, "USD"), 1);
        order.confirm();
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        order.ship();
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    @DisplayName("Should reject confirming empty order")
    void rejectConfirmingEmptyOrder() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> order.confirm());
        assertTrue(ex.getMessage().contains("zero items"));
    }

    @Test
    @DisplayName("Should reject shipping unconfirmed order")
    void rejectShippingUnconfirmedOrder() {
        order.addItem("PROD-1", Money.of(30.00, "USD"), 1);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> order.ship());
        assertTrue(ex.getMessage().contains("must be CONFIRMED before shipping"));
    }

    @Test
    @DisplayName("Should reject applying discount to shipped order")
    void rejectDiscountOnShippedOrder() {
        order.addItem("PROD-1", Money.of(30.00, "USD"), 1);
        order.confirm();
        order.ship();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                order.applyDiscount(Money.of(5.00, "USD"))
        );
        assertTrue(ex.getMessage().contains("Cannot apply discount"));
    }

    @Test
    @DisplayName("Should reject cancelling already shipped order")
    void rejectCancellingShippedOrder() {
        order.addItem("PROD-1", Money.of(30.00, "USD"), 1);
        order.confirm();
        order.ship();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                order.cancel("Customer changed mind")
        );
        assertTrue(ex.getMessage().contains("already been shipped"));
    }
}
