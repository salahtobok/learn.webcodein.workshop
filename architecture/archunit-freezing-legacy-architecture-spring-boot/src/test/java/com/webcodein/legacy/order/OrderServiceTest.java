package com.webcodein.legacy.order;

import com.webcodein.legacy.user.UserController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceTest {

    @Test
    @DisplayName("Verify legacy order creation delegates to UserController")
    void shouldCreateOrderUsingUserController() {
        UserController userController = new UserController();
        OrderService orderService = new OrderService(userController);

        String result = orderService.createOrder();

        assertThat(result).isEqualTo("Order created for: Users List");
    }
}
