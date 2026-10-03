package com.webcodein.legacy.order;

import com.webcodein.legacy.user.UserController;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    // LEGACY VIOLATION: A service should never depend on a Web Controller.
    private final UserController userController;

    public OrderService(UserController userController) {
        this.userController = userController;
    }

    public String createOrder() {
        // Calling controller from service
        String users = userController.getUsers();
        return "Order created for: " + users;
    }
}
