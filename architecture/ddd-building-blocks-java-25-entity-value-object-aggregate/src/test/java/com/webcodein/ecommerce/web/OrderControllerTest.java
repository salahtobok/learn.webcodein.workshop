package com.webcodein.ecommerce.web;

import com.webcodein.ecommerce.domain.Money;
import com.webcodein.ecommerce.domain.Order;
import com.webcodein.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderRepository orderRepository;

    @Test
    @DisplayName("POST /api/orders should return 201 Created with new order")
    void shouldCreateOrder() throws Exception {
        Order order = new Order("ORD-1", "CUST-10");
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders").param("customerId", "CUST-10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("ORD-1"))
                .andExpect(jsonPath("$.customerId").value("CUST-10"));
    }

    @Test
    @DisplayName("GET /api/orders should return list of orders")
    void shouldReturnAllOrders() throws Exception {
        Order o1 = new Order("ORD-1", "CUST-1");
        Order o2 = new Order("ORD-2", "CUST-2");
        when(orderRepository.findAll()).thenReturn(List.of(o1, o2));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("ORD-1"))
                .andExpect(jsonPath("$[1].id").value("ORD-2"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return 200 when found")
    void shouldReturnOrderById() throws Exception {
        Order order = new Order("ORD-1", "CUST-1");
        when(orderRepository.findById("ORD-1")).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/ORD-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ORD-1"))
                .andExpect(jsonPath("$.customerId").value("CUST-1"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return 404 when not found")
    void shouldReturnNotFound() throws Exception {
        when(orderRepository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/NON-EXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/orders/{id}/items should add item and return order")
    void shouldAddItemToOrder() throws Exception {
        Order order = new Order("ORD-1", "CUST-1");
        when(orderRepository.findById("ORD-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/orders/ORD-1/items")
                        .param("productId", "PROD-99")
                        .param("price", "49.99")
                        .param("currency", "USD")
                        .param("qty", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ORD-1"))
                .andExpect(jsonPath("$.items[0].productId").value("PROD-99"))
                .andExpect(jsonPath("$.items[0].price.amount").value(49.99))
                .andExpect(jsonPath("$.items[0].quantity").value(3));
    }
}
