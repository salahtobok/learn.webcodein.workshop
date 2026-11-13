package com.webcodein.workshop.ratelimit;

import com.webcodein.workshop.ratelimit.controller.PublicApiController;
import com.webcodein.workshop.ratelimit.service.ClientResolverService;
import com.webcodein.workshop.ratelimit.service.TokenBucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicApiControllerTest {

    private MockMvc mockMvc;
    private TokenBucketService tokenBucketService;
    private ClientResolverService clientResolverService;

    @BeforeEach
    void setUp() {
        tokenBucketService = new TokenBucketService();
        clientResolverService = new ClientResolverService();
        PublicApiController controller = new PublicApiController(tokenBucketService, clientResolverService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/v1/health should return UP status")
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("GET /api/v1/data within limits returns 200 with headers")
    void shouldAllowRequestWithinLimits() throws Exception {
        mockMvc.perform(get("/api/v1/data")
                        .header("X-API-KEY", "basic_client_123"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-RateLimit-Limit"))
                .andExpect(header().exists("X-RateLimit-Remaining"))
                .andExpect(jsonPath("$.tier").value("BASIC"));
    }

    @Test
    @DisplayName("Exceeding quota triggers 429 Too Many Requests")
    void shouldTrigger429WhenExceeded() throws Exception {
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/v1/data").header("X-Forwarded-For", "192.168.1.50"));
        }

        mockMvc.perform(get("/api/v1/data").header("X-Forwarded-For", "192.168.1.50"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.error").value("Too Many Requests"));
    }
}
