package com.webcodein.boundedcontext.order;

import com.webcodein.boundedcontext.order.dto.CreateOrderRequest;
import com.webcodein.boundedcontext.order.dto.OrderItemRequest;
import com.webcodein.boundedcontext.order.dto.OrderItemResponseDto;
import com.webcodein.boundedcontext.order.dto.OrderResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/orders should return 201 Created with order payload")
    void shouldCreateOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        OrderItemResponseDto itemDto = new OrderItemResponseDto(
            itemId, "SKU-KEY", "Keychron Keyboard", 120.0, 1, 120.0
        );
        OrderResponseDto responseDto = new OrderResponseDto(
            orderId, customerId, "CREATED", Instant.now(), 120.0, List.of(itemDto)
        );

        given(orderService.createOrder(any(CreateOrderRequest.class)))
            .willReturn(responseDto);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "%s",
                        "items": [
                            {
                                "sku": "SKU-KEY",
                                "quantity": 1
                            }
                        ]
                    }
                    """.formatted(customerId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(orderId.toString()))
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.totalAmount").value(120.0))
            .andExpect(jsonPath("$.items[0].sku").value("SKU-KEY"));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/confirm should return 200 OK")
    void shouldConfirmOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        OrderResponseDto responseDto = new OrderResponseDto(
            orderId, customerId, "CONFIRMED", Instant.now(), 120.0, List.of()
        );

        given(orderService.confirmOrder(orderId))
            .willReturn(responseDto);

        mockMvc.perform(post("/api/orders/%s/confirm".formatted(orderId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}
