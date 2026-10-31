package com.webcodein.shop.web;

import com.webcodein.shop.employee.EmployeeRepository;
import com.webcodein.shop.order.Order;
import com.webcodein.shop.order.OrderRepository;
import com.webcodein.shop.order.OrderStatus;
import com.webcodein.shop.product.Product;
import com.webcodein.shop.product.ProductRepository;
import com.webcodein.shop.user.UserRepository;
import com.webcodein.shop.user.UserSummary;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShopController {

    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public ShopController(
            ProductRepository productRepository,
            EmployeeRepository employeeRepository,
            OrderRepository orderRepository,
            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.employeeRepository = employeeRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProductsByManufacturer(@RequestParam("manufacturer") String manufacturer) {
        List<Product> products = productRepository.findByManufacturer(manufacturer);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/employees/ranks")
    public ResponseEntity<List<Object[]>> getEmployeeDepartmentRanks() {
        List<Object[]> ranks = employeeRepository.findEmployeesWithDepartmentRank();
        return ResponseEntity.ok(ranks);
    }

    @GetMapping("/users/active")
    public ResponseEntity<List<UserSummary>> getActiveUsers() {
        List<UserSummary> summaries = userRepository.findByIsActiveTrue();
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/orders/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable("status") OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return ResponseEntity.ok(orders);
    }
}
