package com.webcodein.cqrs;

import com.webcodein.cqrs.query.OrderReadModel;
import com.webcodein.cqrs.query.OrderReadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CqrsApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderReadRepository repository;

    @Test
    void testCqrsFlow() throws Exception {
        String response = mockMvc.perform(post("/api/orders?customerId=C123&amount=99.99"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UUID orderId = UUID.fromString(response.replace("\"", ""));
        
        OrderReadModel model = repository.findById(orderId).orElseThrow();
        assertThat(model.getStatus()).isEqualTo("PENDING");

        mockMvc.perform(post("/api/orders/" + orderId + "/confirm"))
                .andExpect(status().isOk());

        OrderReadModel updatedModel = repository.findById(orderId).orElseThrow();
        assertThat(updatedModel.getStatus()).isEqualTo("CONFIRMED");
    }
}

