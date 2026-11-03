package com.webcodein.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Verify Hexagonal Architecture (Ports & Adapters) user creation workflow")
    void testHexagonal() throws Exception {
        mockMvc.perform(post("/hex/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"1\", \"name\":\"Test\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verify Clean Architecture use case order placement workflow")
    void testClean() throws Exception {
        mockMvc.perform(post("/clean/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verify Onion Architecture core domain product creation workflow")
    void testOnion() throws Exception {
        mockMvc.perform(post("/onion/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"1\"}"))
                .andExpect(status().isOk());
    }
}
