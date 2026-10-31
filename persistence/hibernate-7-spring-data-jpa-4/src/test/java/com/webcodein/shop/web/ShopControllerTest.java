package com.webcodein.shop.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.webcodein.shop.employee.EmployeeRepository;
import com.webcodein.shop.order.Order;
import com.webcodein.shop.order.OrderRepository;
import com.webcodein.shop.order.OrderStatus;
import com.webcodein.shop.product.Product;
import com.webcodein.shop.product.ProductMetadata;
import com.webcodein.shop.product.ProductRepository;
import com.webcodein.shop.user.UserRepository;
import com.webcodein.shop.user.UserSummary;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShopController.class)
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("GET /api/products/search should return matching products")
    void searchProducts() throws Exception {
        Product p = new Product(10L, "DevLaptop", new ProductMetadata("Acme", 3, List.of("dev")));
        given(productRepository.findByManufacturer("Acme")).willReturn(List.of(p));

        mockMvc.perform(get("/api/products/search").param("manufacturer", "Acme"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("DevLaptop"))
                .andExpect(jsonPath("$[0].metadata.manufacturer").value("Acme"))
                .andExpect(jsonPath("$[0].metadata.warrantyYears").value(3));
    }

    @Test
    @DisplayName("GET /api/employees/ranks should return calculated department ranks")
    void getEmployeeRanks() throws Exception {
        Object[] rank1 = new Object[]{"Alice", new BigDecimal("150000"), 1L};
        given(employeeRepository.findEmployeesWithDepartmentRank()).willReturn(Collections.singletonList(rank1));

        mockMvc.perform(get("/api/employees/ranks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0][0]").value("Alice"))
                .andExpect(jsonPath("$[0][1]").value(150000))
                .andExpect(jsonPath("$[0][2]").value(1));
    }

    @Test
    @DisplayName("GET /api/users/active should return user summary projections")
    void getActiveUsers() throws Exception {
        UserSummary user = new UserSummary(1L, "ada", "ada@example.com");
        given(userRepository.findByIsActiveTrue()).willReturn(List.of(user));

        mockMvc.perform(get("/api/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("ada"))
                .andExpect(jsonPath("$[0].email").value("ada@example.com"));
    }

    @Test
    @DisplayName("GET /api/orders/{status} should return matching orders")
    void getOrdersByStatus() throws Exception {
        Order order = new Order(OrderStatus.PAID);
        given(orderRepository.findByStatus(OrderStatus.PAID)).willReturn(List.of(order));

        mockMvc.perform(get("/api/orders/PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PAID"));
    }
}
