package com.webcodein.workshop.architecture.archunitenforceddd;

import com.webcodein.workshop.architecture.archunitenforceddd.application.service.OrderService;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Money;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Order;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.OrderLine;
import com.webcodein.workshop.architecture.archunitenforceddd.presentation.controller.OrderController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController WebMvc Slice Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/orders should return 201 Created")
    void shouldCreateOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, "CUST-99");
        order.addLine(new OrderLine(UUID.randomUUID(), "ITEM-1", new Money(new BigDecimal("100.00"), "USD")));

        when(orderService.createOrder(eq("CUST-99"), eq("ITEM-1"), any(BigDecimal.class), eq("USD")))
                .thenReturn(order);

        String jsonPayload = """
                {
                    "customerId": "CUST-99",
                    "productCode": "ITEM-1",
                    "price": 100.00,
                    "currency": "USD"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value("CUST-99"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return 200 OK when found")
    void shouldGetOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, "CUST-99");

        when(orderService.getOrder(orderId)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value("CUST-99"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return 404 when not found")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrder(orderId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isNotFound());
    }
}
