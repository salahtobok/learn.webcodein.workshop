package com.webcodein.cqrs.query;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderSummaryViewTest {

    @Test
    void shouldCreateOrderSummaryViewWithFields() {
        OrderSummaryView view = new OrderSummaryView(101L, "CUST-200", 99.50, "Pending Fulfillment");

        assertThat(view.getOrderId()).isEqualTo(101L);
        assertThat(view.getCustomerId()).isEqualTo("CUST-200");
        assertThat(view.getTotalAmount()).isEqualTo(99.50);
        assertThat(view.getDisplayStatus()).isEqualTo("Pending Fulfillment");
    }

    @Test
    void shouldSupportNoArgsConstructorForJpa() {
        OrderSummaryView view = new OrderSummaryView();
        assertThat(view.getOrderId()).isNull();
    }
}
