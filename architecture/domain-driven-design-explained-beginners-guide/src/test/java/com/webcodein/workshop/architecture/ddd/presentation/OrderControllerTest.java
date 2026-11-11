package com.webcodein.workshop.architecture.ddd.presentation;

import com.webcodein.workshop.architecture.ddd.application.AddItemCommand;
import com.webcodein.workshop.architecture.ddd.application.CreateOrderCommand;
import com.webcodein.workshop.architecture.ddd.application.OrderApplicationService;
import com.webcodein.workshop.architecture.ddd.application.OrderDto;
import com.webcodein.workshop.architecture.ddd.application.OrderLineDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
@DisplayName("Presentation Layer: OrderController MockMvc Slice Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderApplicationService orderApplicationService;

    @Test
    @DisplayName("POST /api/orders should return 201 Created with order payload")
    void shouldCreateOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderDto responseDto = new OrderDto(
                orderId,
                "customer@example.com",
                "DRAFT",
                BigDecimal.ZERO,
                "USD",
                Instant.now(),
                List.of()
        );

        when(orderApplicationService.createOrder(any(CreateOrderCommand.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerEmail": "customer@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.customerEmail").value("customer@example.com"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/items should add item and return 200 OK")
    void shouldAddItemToOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderLineDto lineDto = new OrderLineDto(
                UUID.randomUUID(),
                "BOOK-001",
                2,
                new BigDecimal("29.99"),
                new BigDecimal("59.98"),
                "USD"
        );
        OrderDto responseDto = new OrderDto(
                orderId,
                "customer@example.com",
                "DRAFT",
                new BigDecimal("59.98"),
                "USD",
                Instant.now(),
                List.of(lineDto)
        );

        when(orderApplicationService.addItem(eq(orderId), any(AddItemCommand.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/orders/{id}/items", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "bookId": "BOOK-001",
                                    "quantity": 2,
                                    "unitPrice": 29.99,
                                    "currency": "USD"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(59.98))
                .andExpect(jsonPath("$.lines[0].bookId").value("BOOK-001"))
                .andExpect(jsonPath("$.lines[0].quantity").value(2));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/confirm should confirm order and return 200 OK")
    void shouldConfirmOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderDto responseDto = new OrderDto(
                orderId,
                "customer@example.com",
                "CONFIRMED",
                new BigDecimal("59.98"),
                "USD",
                Instant.now(),
                List.of()
        );

        when(orderApplicationService.confirmOrder(orderId)).thenReturn(responseDto);

        mockMvc.perform(post("/api/orders/{id}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return 200 OK when found")
    void shouldGetOrderById() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderDto responseDto = new OrderDto(
                orderId,
                "customer@example.com",
                "DRAFT",
                BigDecimal.ZERO,
                "USD",
                Instant.now(),
                List.of()
        );

        when(orderApplicationService.getOrder(orderId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return 404 Not Found when order does not exist")
    void shouldReturn404WhenOrderNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderApplicationService.getOrder(orderId))
                .thenThrow(new NoSuchElementException("Order not found with ID: " + orderId));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found with ID: " + orderId));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/confirm should return 409 Conflict when order cannot be confirmed")
    void shouldReturn409OnConflict() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderApplicationService.confirmOrder(orderId))
                .thenThrow(new IllegalStateException("Cannot confirm an order without items"));

        mockMvc.perform(post("/api/orders/{id}/confirm", orderId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Cannot confirm an order without items"));
    }
}
