package com.webcodein.ddd.infrastructure.web;

import com.webcodein.ddd.domain.model.order.Order;
import com.webcodein.ddd.domain.model.order.OrderStatus;
import com.webcodein.ddd.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldCompleteOrderFlow() throws Exception {
        UUID customerId = UUID.randomUUID();
        
        // 1. Create Order
        String orderIdStr = mockMvc.perform(post("/api/orders")
                        .param("customerId", customerId.toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().replace("\"", "");

        UUID orderId = UUID.fromString(orderIdStr);

        // 2. Add Item
        mockMvc.perform(post("/api/orders/" + orderId + "/items")
                        .param("productId", UUID.randomUUID().toString())
                        .param("quantity", "2")
                        .param("price", "99.99"))
                .andExpect(status().isOk());

        // 3. Confirm Order
        mockMvc.perform(post("/api/orders/" + orderId + "/confirm"))
                .andExpect(status().isOk());

        // 4. Verify in DB
        Order savedOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderStatus.CONFIRMED, savedOrder.getStatus());
        assertEquals(1, savedOrder.getItems().size());
    }
}
