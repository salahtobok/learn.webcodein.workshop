package com.webcodein.workshop.kafka.controller;

import com.webcodein.workshop.kafka.model.OrderEvent;
import com.webcodein.workshop.kafka.service.KafkaRetryConsumerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DltAdminControllerTest {

    private MockMvc mockMvc;
    private KafkaRetryConsumerService consumerService;

    @BeforeEach
    void setUp() {
        consumerService = new KafkaRetryConsumerService();
        DltAdminController controller = new DltAdminController(consumerService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /admin/dlt/stats should return initial stats")
    void shouldReturnInitialStats() throws Exception {
        mockMvc.perform(get("/admin/dlt/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcessed").value(0))
                .andExpect(jsonPath("$.deadLetterCount").value(0));
    }

    @Test
    @DisplayName("POST /admin/dlt/simulate should process order successfully")
    void shouldSimulateOrderProcessing() throws Exception {
        String payload = """
                {
                    "orderId": "ORD-200",
                    "customerId": "CUST-99",
                    "amount": 149.99,
                    "eventType": "ORDER_CREATED",
                    "retryCount": 0
                }
                """;

        mockMvc.perform(post("/admin/dlt/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ORD-200"))
                .andExpect(jsonPath("$.action").value("PROCESSED_SUCCESSFULLY"));
    }

    @Test
    @DisplayName("POST /admin/dlt/simulate with poison pill should create DLT record")
    void shouldCreateDltRecordOnPoisonPill() throws Exception {
        String poisonPayload = """
                {
                    "orderId": "ORD-POISON-X",
                    "customerId": "CUST-99",
                    "amount": 50.00,
                    "eventType": "ORDER_CREATED",
                    "retryCount": 0
                }
                """;

        mockMvc.perform(post("/admin/dlt/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(poisonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.action").value("ROUTED_TO_DLT_FATAL"));

        mockMvc.perform(get("/admin/dlt/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].originalTopic").value("orders.v1"))
                .andExpect(jsonPath("$[0].replayed").value(false));
    }

    @Test
    @DisplayName("POST /admin/dlt/replay/{id} should replay message")
    void shouldReplayDltMessage() throws Exception {
        consumerService.processOrderEvent(new OrderEvent("ORD-POISON-1", "CUST-1", new BigDecimal("10.00"), "CREATED", 0));
        String dltId = consumerService.getAllDltMessages().getFirst().id();

        mockMvc.perform(post("/admin/dlt/replay/" + dltId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REPLAYED_TO_MAIN_TOPIC"));
    }
}
