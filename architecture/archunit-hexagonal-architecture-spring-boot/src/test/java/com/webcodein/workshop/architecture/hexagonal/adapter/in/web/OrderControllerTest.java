package com.webcodein.workshop.architecture.hexagonal.adapter.in.web;

import com.webcodein.workshop.architecture.hexagonal.application.port.in.GetOrderUseCase;
import com.webcodein.workshop.architecture.hexagonal.application.port.in.PlaceOrderUseCase;
import com.webcodein.workshop.architecture.hexagonal.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

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
    private PlaceOrderUseCase placeOrderUseCase;

    @MockitoBean
    private GetOrderUseCase getOrderUseCase;

    @Test
    @DisplayName("POST /api/orders should return HTTP 200 and generated UUID")
    void shouldPlaceOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(placeOrderUseCase.placeOrder(any(Order.class))).thenReturn(orderId);

        String json = """
            {
                "product": "Spring Boot in Action",
                "quantity": 2
            }
            """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return HTTP 200 with order details when found")
    void shouldReturnOrderWhenFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, "UltraWide Monitor", 1);
        when(getOrderUseCase.getOrder(orderId)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.product").value("UltraWide Monitor"))
                .andExpect(jsonPath("$.quantity").value(1));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return HTTP 404 when order does not exist")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(getOrderUseCase.getOrder(orderId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isNotFound());
    }
}
