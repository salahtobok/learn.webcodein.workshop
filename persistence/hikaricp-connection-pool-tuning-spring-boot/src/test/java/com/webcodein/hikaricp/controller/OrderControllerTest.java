package com.webcodein.hikaricp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.hikaricp.dto.OrderRequest;
import com.webcodein.hikaricp.entity.ProductOrder;
import com.webcodein.hikaricp.service.OrderProcessingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderProcessingService orderService;

    @Test
    @DisplayName("POST /api/orders should return HTTP 201 with created order")
    void shouldCreateOrder() throws Exception {
        OrderRequest request = new OrderRequest("Alice Walker", "PROD-101", new BigDecimal("149.99"));
        ProductOrder created = new ProductOrder("Alice Walker", "PROD-101", new BigDecimal("149.99"), "CONFIRMED", Instant.now());
        created.setId(42L);

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.customerName").value("Alice Walker"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /api/orders/slow should execute slow query simulation")
    void shouldSimulateSlowQuery() throws Exception {
        mockMvc.perform(get("/api/orders/slow").param("delaySeconds", "0.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.simulatedDelaySeconds").value(0.5));

        verify(orderService).processSlowOrder(0.5);
    }

    @Test
    @DisplayName("GET /api/orders/leak should trigger leak simulation message")
    void shouldSimulateLeak() throws Exception {
        when(orderService.simulateConnectionLeak()).thenReturn("Connection leak simulated");

        mockMvc.perform(get("/api/orders/leak"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Connection leak simulated"));

        verify(orderService).simulateConnectionLeak();
    }

    @Test
    @DisplayName("POST /api/orders/leak/release should trigger release of leaked connections")
    void shouldReleaseLeakedConnections() throws Exception {
        mockMvc.perform(post("/api/orders/leak/release"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All simulated leaked connections released"));

        verify(orderService).releaseLeakedConnections();
    }
}
