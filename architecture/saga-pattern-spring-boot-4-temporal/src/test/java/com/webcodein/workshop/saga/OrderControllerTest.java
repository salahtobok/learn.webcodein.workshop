package com.webcodein.workshop.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@TestPropertySource(properties = "temporal.worker.enabled=false")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private WorkflowClient workflowClient;

    @Test
    @DisplayName("POST /api/orders should return HTTP 200 when Saga completes successfully")
    void shouldReturn200WhenSagaSucceeds() throws Exception {
        OrderFulfillmentWorkflow workflowMock = mock(OrderFulfillmentWorkflow.class);
        when(workflowClient.newWorkflowStub(eq(OrderFulfillmentWorkflow.class), any(WorkflowOptions.class)))
                .thenReturn(workflowMock);

        OrderController.CreateOrderRequest request = new OrderController.CreateOrderRequest("order-101", "cust-77", 149.99);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.orderId").value("order-101"))
                .andExpect(jsonPath("$.workflowId").value("order-saga-order-101"));

        verify(workflowMock).fulfillOrder("order-101", "cust-77", 149.99);
    }

    @Test
    @DisplayName("POST /api/orders should return HTTP 400 when request payload is invalid")
    void shouldReturn400WhenPayloadInvalid() throws Exception {
        OrderController.CreateOrderRequest invalidRequest = new OrderController.CreateOrderRequest(null, "cust-77", -10.0);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(workflowClient);
    }

    @Test
    @DisplayName("POST /api/orders should return HTTP 500 with COMPENSATED status when Saga fails")
    void shouldReturn500WhenSagaFails() throws Exception {
        OrderFulfillmentWorkflow workflowMock = mock(OrderFulfillmentWorkflow.class);
        doThrow(new RuntimeException("Out of stock!"))
                .when(workflowMock).fulfillOrder("order-fail", "cust-77", 99.0);

        when(workflowClient.newWorkflowStub(eq(OrderFulfillmentWorkflow.class), any(WorkflowOptions.class)))
                .thenReturn(workflowMock);

        OrderController.CreateOrderRequest request = new OrderController.CreateOrderRequest("order-fail", "cust-77", 99.0);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("COMPENSATED"))
                .andExpect(jsonPath("$.orderId").value("order-fail"));
    }
}
