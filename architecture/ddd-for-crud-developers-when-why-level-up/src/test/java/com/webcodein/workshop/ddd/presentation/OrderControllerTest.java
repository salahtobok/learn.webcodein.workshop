package com.webcodein.workshop.ddd.presentation;

import com.webcodein.workshop.ddd.application.OrderApplicationService;
import com.webcodein.workshop.ddd.domain.model.CustomerId;
import com.webcodein.workshop.ddd.domain.model.Money;
import com.webcodein.workshop.ddd.domain.model.Order;
import com.webcodein.workshop.ddd.domain.model.OrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController REST API Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderApplicationService orderService;

    @Test
    @DisplayName("POST /api/orders returns 201 Created with Location header and OrderResponse body")
    void shouldCreateOrderSuccessfully() throws Exception {
        UUID orderUuid = UUID.randomUUID();
        UUID customerUuid = UUID.randomUUID();
        OrderId orderId = OrderId.of(orderUuid);

        Order order = new Order(orderId, CustomerId.of(customerUuid), "USD");
        order.addItem("PROD-01", Money.of(99.99, "USD"), 1);

        when(orderService.createOrder(any(), eq("USD"), eq("PROD-01"), any(), eq(1))).thenReturn(orderId);
        when(orderService.getOrder(orderId)).thenReturn(order);

        String jsonPayload = """
                {
                    "customerId": "%s",
                    "currency": "USD",
                    "productCode": "PROD-01",
                    "unitPrice": 99.99,
                    "quantity": 1
                }
                """.formatted(customerUuid);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/orders/" + orderUuid))
                .andExpect(jsonPath("$.orderId").value(orderUuid.toString()))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(99.99));
    }

    @Test
    @DisplayName("GET /api/orders/{id} returns 200 OK with order details")
    void shouldReturnOrderDetails() throws Exception {
        UUID orderUuid = UUID.randomUUID();
        UUID customerUuid = UUID.randomUUID();
        OrderId orderId = OrderId.of(orderUuid);

        Order order = new Order(orderId, CustomerId.of(customerUuid), "EUR");
        order.addItem("WIDGET-X", Money.of(45.00, "EUR"), 2);

        when(orderService.getOrder(orderId)).thenReturn(order);

        mockMvc.perform(get("/api/orders/" + orderUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderUuid.toString()))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.subtotal").value(90.0))
                .andExpect(jsonPath("$.totalAmount").value(90.0));
    }

    @Test
    @DisplayName("GET /api/orders/{id} returns 404 Not Found when order is absent")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID orderUuid = UUID.randomUUID();
        OrderId orderId = OrderId.of(orderUuid);

        when(orderService.getOrder(orderId)).thenThrow(new NoSuchElementException("Order not found with ID: " + orderId));

        mockMvc.perform(get("/api/orders/" + orderUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/orders/{id}/discount returns 400 Bad Request when discount exceeds total")
    void shouldReturn400OnExcessiveDiscount() throws Exception {
        UUID orderUuid = UUID.randomUUID();
        OrderId orderId = OrderId.of(orderUuid);

        doThrow(new IllegalArgumentException("Discount amount exceeds order subtotal"))
                .when(orderService).applyDiscount(eq(orderId), any());

        String jsonPayload = """
                {
                    "amount": 500.00,
                    "currency": "USD"
                }
                """;

        mockMvc.perform(post("/api/orders/" + orderUuid + "/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Discount amount exceeds order subtotal"));
    }
}
