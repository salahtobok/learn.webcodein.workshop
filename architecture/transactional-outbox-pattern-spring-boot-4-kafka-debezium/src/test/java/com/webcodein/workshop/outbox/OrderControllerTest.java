package com.webcodein.workshop.outbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @Test
    @DisplayName("POST /api/orders should return 201 Created with order payload")
    void shouldCreateOrder() throws Exception {
        Order created = new Order(1L, "Monitor 4K", new BigDecimal("499.00"));
        when(orderService.placeOrder(eq("Monitor 4K"), any(BigDecimal.class))).thenReturn(created);

        String jsonPayload = """
                {
                    "product": "Monitor 4K",
                    "price": 499.00
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.product").value("Monitor 4K"))
                .andExpect(jsonPath("$.price").value(499.00));
    }

    @Test
    @DisplayName("GET /api/orders should return list of orders")
    void shouldReturnAllOrders() throws Exception {
        when(orderRepository.findAll()).thenReturn(List.of(
                new Order(1L, "Item 1", new BigDecimal("10.00")),
                new Order(2L, "Item 2", new BigDecimal("20.00"))
        ));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].product").value("Item 1"))
                .andExpect(jsonPath("$[1].product").value("Item 2"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return 200 if found")
    void shouldReturnOrderById() throws Exception {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order(1L, "Item 1", new BigDecimal("10.00"))));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.product").value("Item 1"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return 404 if not found")
    void shouldReturnNotFound() throws Exception {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }
}
