package com.webcodein.cqrs.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderCommandController.class)
class OrderCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderCommandService orderCommandService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldPlaceOrderSuccessfully() throws Exception {
        Order savedOrder = new Order("CUST-500", 350.00, "CREATED");
        ReflectionTestUtils.setField(savedOrder, "id", 55L);

        when(orderCommandService.placeOrder(any(OrderRequest.class))).thenReturn(savedOrder);

        OrderRequest request = new OrderRequest();
        request.setCustomerId("CUST-500");
        request.setTotalAmount(350.00);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(55))
                .andExpect(jsonPath("$.customerId").value("CUST-500"))
                .andExpect(jsonPath("$.totalAmount").value(350.00))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(orderCommandService).placeOrder(any(OrderRequest.class));
    }
}
