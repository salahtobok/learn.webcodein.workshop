package com.webcodein.workshop.crud;

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

@WebMvcTest(CrudOrderController.class)
@AutoConfigureMockMvc
@DisplayName("CRUD Pattern: CrudOrderController WebMvc Slice Tests")
class CrudOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/crud/orders should return 201 Created")
    void shouldCreateOrder() throws Exception {
        OrderEntity entity = new OrderEntity("buyer@example.com", BigDecimal.valueOf(120.0), "CREATED");
        entity.setId(1L);
        when(orderService.createOrder(eq("buyer@example.com"), any(BigDecimal.class))).thenReturn(entity);

        mockMvc.perform(post("/api/crud/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerEmail": "buyer@example.com",
                                  "totalAmount": 120.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/crud/orders/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerEmail").value("buyer@example.com"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("GET /api/crud/orders/{id} should return 200 OK")
    void shouldGetOrder() throws Exception {
        OrderEntity entity = new OrderEntity("buyer@example.com", BigDecimal.valueOf(120.0), "CREATED");
        entity.setId(1L);
        when(orderService.findById(1L)).thenReturn(entity);

        mockMvc.perform(get("/api/crud/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("GET /api/crud/orders/{id} should return 404 when not found")
    void shouldReturn404WhenNotFound() throws Exception {
        when(orderService.findById(999L)).thenThrow(new NoSuchElementException("Order not found: 999"));

        mockMvc.perform(get("/api/crud/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("PUT /api/crud/orders/{id}/fulfill should return 200 OK")
    void shouldFulfillOrder() throws Exception {
        mockMvc.perform(put("/api/crud/orders/1/fulfill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order fulfilled successfully"));
    }

    @Test
    @DisplayName("PUT /api/crud/orders/{id}/fulfill should return 409 Conflict when order cannot be fulfilled")
    void shouldReturn409WhenFulfillFails() throws Exception {
        doThrow(new IllegalStateException("Order is not CREATED")).when(orderService).fulfillOrder(1L);

        mockMvc.perform(put("/api/crud/orders/1/fulfill"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }
}
