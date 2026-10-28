package com.webcodein.security.zerotrust;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FinancialSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Health endpoint is accessible without authentication (PermitAll)")
    void healthEndpointPermitAll() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Financial balance endpoint rejects unauthenticated request with 401")
    void balanceEndpointRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/acc-100/balance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Financial balance endpoint rejects token missing finance:read scope with 403")
    void balanceEndpointRejectsMissingScope() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/acc-100/balance")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("acc-100")
                                .claim("scope", "audit:read")
                        )))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Financial balance endpoint rejects token when subject does not match accountId with 403")
    void balanceEndpointRejectsNonOwner() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/acc-100/balance")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("acc-200")
                                .claim("scope", "finance:read")
                        )))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Financial balance endpoint returns 200 and balance when scope and owner match")
    void balanceEndpointAllowsOwnerWithFinanceScope() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/acc-100/balance")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("acc-100")
                                .claim("scope", "finance:read")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("acc-100"))
                .andExpect(jsonPath("$.balance").value(15000.00))
                .andExpect(jsonPath("$.currency").value("USD"));
    }
}
