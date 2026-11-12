package com.webcodein.ecommerce.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("Should create order with generated UUID and customerId")
    void shouldCreateOrder() {
        Order order = new Order("CUST-42");

        assertThat(order.getId()).isNotBlank();
        assertThat(order.getCustomerId()).isEqualTo("CUST-42");
        assertThat(order.getItems()).isEmpty();
    }

    @Test
    @DisplayName("Adding item should increase items collection inside aggregate boundary")
    void shouldAddItem() {
        Order order = new Order("CUST-42");
        order.addItem("PROD-1", Money.of(new BigDecimal("99.99"), "USD"), 2);

        assertThat(order.getItems()).hasSize(1);
        OrderItem item = order.getItems().getFirst();
        assertThat(item.getProductId()).isEqualTo("PROD-1");
        assertThat(item.getPrice().amount()).isEqualTo(new BigDecimal("99.99"));
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Adding item with non-positive quantity should throw IllegalArgumentException")
    void shouldThrowWhenQuantityNonPositive() {
        Order order = new Order("CUST-42");

        assertThatThrownBy(() -> order.addItem("PROD-1", Money.of(new BigDecimal("10.00"), "USD"), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");

        assertThatThrownBy(() -> order.addItem("PROD-1", Money.of(new BigDecimal("10.00"), "USD"), -5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Items collection should be unmodifiable from outside aggregate root")
    void shouldProtectItemsFromExternalModification() {
        Order order = new Order("CUST-42");
        order.addItem("PROD-1", Money.of(new BigDecimal("10.00"), "USD"), 1);

        assertThatThrownBy(() -> order.getItems().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Calculate total on empty order should return zero Money")
    void shouldCalculateZeroTotalOnEmptyOrder() {
        Order order = new Order("CUST-42");
        Money total = order.calculateTotal();

        assertThat(total.amount()).isEqualTo(BigDecimal.ZERO);
        assertThat(total.currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Calculate total should correctly sum product price times quantity")
    void shouldCalculateTotalAcrossItems() {
        Order order = new Order("CUST-42");
        order.addItem("PROD-A", Money.of(new BigDecimal("50.00"), "USD"), 2); // 100.00
        order.addItem("PROD-B", Money.of(new BigDecimal("25.50"), "USD"), 4); // 102.00

        Money total = order.calculateTotal();

        assertThat(total.amount()).isEqualByComparingTo(new BigDecimal("202.00"));
        assertThat(total.currency()).isEqualTo("USD");
    }
}
