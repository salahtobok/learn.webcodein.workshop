package com.webcodein.workshop.ddd.presentation;

import com.webcodein.workshop.ddd.application.CreateOrderCommand;
import com.webcodein.workshop.ddd.application.OrderApplicationService;
import com.webcodein.workshop.ddd.application.OrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DddOrderController.class)
@AutoConfigureMockMvc
@DisplayName("DDD Pattern: DddOrderController WebMvc Slice Tests")
class DddOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderApplicationService appService;

    @Test
    @DisplayName("POST /api/ddd/orders should return 201 Created")
    void shouldCreateOrder() throws Exception {
        when(appService.createOrder(any(CreateOrderCommand.class))).thenReturn(42L);

        mockMvc.perform(post("/api/ddd/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "customer@domain.com",
                                  "amount": 250.00,
                                  "currency": "USD"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/ddd/orders/42"))
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    @DisplayName("GET /api/ddd/orders/{id} should return 200 OK with OrderDto")
    void shouldGetOrder() throws Exception {
        OrderDto dto = new OrderDto(42L, "customer@domain.com", BigDecimal.valueOf(250.0), "USD", "CREATED");
        when(appService.getOrder(42L)).thenReturn(dto);

        mockMvc.perform(get("/api/ddd/orders/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.customerEmail").value("customer@domain.com"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("GET /api/ddd/orders/{id} should return 404 when not found")
    void shouldReturn404WhenNotFound() throws Exception {
        when(appService.getOrder(999L)).thenThrow(new NoSuchElementException("Order not found: 999"));

        mockMvc.perform(get("/api/ddd/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("PUT /api/ddd/orders/{id}/fulfill should return 200 OK")
    void shouldFulfillOrder() throws Exception {
        mockMvc.perform(put("/api/ddd/orders/42/fulfill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order fulfilled via Domain Aggregate"));
    }

    @Test
    @DisplayName("PUT /api/ddd/orders/{id}/cancel should return 409 Conflict when cancellation violates invariant")
    void shouldReturn409WhenCancellationFails() throws Exception {
        doThrow(new IllegalStateException("Cannot cancel an order that has already been fulfilled"))
                .when(appService).cancelOrder(eq(42L), any(String.class));

        mockMvc.perform(put("/api/ddd/orders/42/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reason": "Late delivery"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }
}
