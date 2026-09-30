package com.webcodein.ecommerce.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Test
    void shouldPlaceOrderAndTriggerInventoryEvent() {
        Order order = orderService.placeOrder("LAPTOP-X", 1);
        assertThat(order).isNotNull();
        assertThat(order.getStatus()).isEqualTo("PENDING");
    }
}

