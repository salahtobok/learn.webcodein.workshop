package com.webcodein.workshop.dddmistakes.ecommerce.api.controller;

import com.webcodein.workshop.dddmistakes.ecommerce.domain.Order;
import com.webcodein.workshop.dddmistakes.ecommerce.domain.OrderRepository;
import com.webcodein.workshop.dddmistakes.ecommerce.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController REST API Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderRepository repository;

    @Test
    @DisplayName("GET /api/orders/{id} returns 200 OK with dedicated DTO payload")
    void shouldReturnOrderResponseDto() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, "CUST-42");
        order.addItem(new Product(UUID.randomUUID(), new BigDecimal("120.00")), 1);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.total").value("120.00"))
                .andExpect(jsonPath("$.friendlyStatus").value("CREATED"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} returns 404 Not Found when order does not exist")
    void shouldReturn404WhenOrderNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(repository.findById(orderId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isNotFound());
    }
}
