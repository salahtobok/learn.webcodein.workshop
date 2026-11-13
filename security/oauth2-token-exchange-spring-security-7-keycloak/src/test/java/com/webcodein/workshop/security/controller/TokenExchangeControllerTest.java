package com.webcodein.workshop.security.controller;

import com.webcodein.workshop.security.service.TokenExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TokenExchangeControllerTest {

    private MockMvc mockMvc;
    private TokenExchangeService tokenExchangeService;

    @BeforeEach
    void setUp() {
        tokenExchangeService = new TokenExchangeService();
        TokenExchangeController controller = new TokenExchangeController(tokenExchangeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /oauth/token should perform RFC 8693 token exchange")
    void shouldPerformTokenExchange() throws Exception {
        String payload = """
                {
                    "grantType": "urn:ietf:params:oauth:grant-type:token-exchange",
                    "subjectToken": "jwt_token_alice_prod",
                    "subjectTokenType": "urn:ietf:params:oauth:token-type:jwt",
                    "actorToken": "jwt_token_gateway_prod",
                    "actorTokenType": "urn:ietf:params:oauth:token-type:jwt",
                    "requestedTokenType": "urn:ietf:params:oauth:token-type:access_token",
                    "audience": "payment-service",
                    "scope": "payments:transfer"
                }
                """;

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(300))
                .andExpect(jsonPath("$.scope").value("payments:transfer"));
    }

    @Test
    @DisplayName("GET /admin/token-exchange/stats should return stats")
    void shouldReturnExchangeStats() throws Exception {
        mockMvc.perform(get("/admin/token-exchange/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.standard").value("RFC_8693"));
    }

    @Test
    @DisplayName("GET /admin/token-exchange/audit should return audit trail")
    void shouldReturnAuditTrail() throws Exception {
        String payload = """
                {
                    "grantType": "urn:ietf:params:oauth:grant-type:token-exchange",
                    "subjectToken": "jwt_alice",
                    "subjectTokenType": "urn:ietf:params:oauth:token-type:jwt",
                    "audience": "orders-service",
                    "scope": "orders:create"
                }
                """;

        mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/token-exchange/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("user:alice"))
                .andExpect(jsonPath("$[0].audience").value("orders-service"));
    }
}
