package com.webcodein.ddd.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.ddd.application.dto.AddressDto;
import com.webcodein.ddd.application.dto.CreateOrderRequest;
import com.webcodein.ddd.application.dto.OrderItemDto;
import com.webcodein.ddd.application.dto.OrderResponse;
import com.webcodein.ddd.application.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/orders returns HTTP 201 with created aggregate payload")
    void shouldCreateOrderViaApi() throws Exception {
        UUID orderId = UUID.randomUUID();
        AddressDto address = new AddressDto("100 Innovation Way", "Austin", "78701");
        List<OrderItemDto> items = List.of(new OrderItemDto("Wireless Mouse", 1, new BigDecimal("49.99")));
        CreateOrderRequest request = new CreateOrderRequest(address, items);

        OrderResponse mockResponse = new OrderResponse(
                orderId,
                "PENDING",
                new BigDecimal("49.99"),
                address,
                items
        );

        given(orderService.createOrder(any(CreateOrderRequest.class))).willReturn(mockResponse);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(49.99))
                .andExpect(jsonPath("$.shippingAddress.city").value("Austin"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} returns HTTP 200 with aggregate details")
    void shouldGetOrderByIdViaApi() throws Exception {
        UUID orderId = UUID.randomUUID();
        AddressDto address = new AddressDto("200 Tech Hub", "Denver", "80202");
        OrderResponse mockResponse = new OrderResponse(
                orderId,
                "CONFIRMED",
                new BigDecimal("120.00"),
                address,
                List.of(new OrderItemDto("Webcam", 1, new BigDecimal("120.00")))
        );

        given(orderService.getOrderById(orderId)).willReturn(mockResponse);

        mockMvc.perform(get("/api/orders/{id}", orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalAmount").value(120.00));
    }
}
