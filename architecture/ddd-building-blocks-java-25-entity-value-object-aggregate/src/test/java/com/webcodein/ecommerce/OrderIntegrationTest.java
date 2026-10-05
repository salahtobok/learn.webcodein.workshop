package com.webcodein.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateOrderAndAddItem() throws Exception {
        String response = mockMvc.perform(post("/api/orders").param("customerId", "CUST-100"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Workaround to extract ID without Jackson explicitly
        String orderId = response.split("\"id\":\"")[1].split("\"")[0];

        mockMvc.perform(post("/api/orders/" + orderId + "/items")
                        .param("productId", "PROD-55")
                        .param("price", "99.99")
                        .param("currency", "USD")
                        .param("qty", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productId").value("PROD-55"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }
}
