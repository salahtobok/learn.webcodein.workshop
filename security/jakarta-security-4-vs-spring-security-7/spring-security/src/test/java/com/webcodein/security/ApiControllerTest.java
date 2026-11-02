package com.webcodein.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
@Import(SecurityConfig.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("GET /api/public/hello should permit all unauthenticated requests with HTTP 200")
    void publicEndpointAllowsUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/public/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, everyone"));
    }

    @Test
    @DisplayName("GET /api/admin without authentication should return HTTP 401 Unauthorized")
    void adminEndpointRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/admin with non-admin authority should return HTTP 403 Forbidden")
    void adminEndpointRejectsNonAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin with ROLE_ADMIN should return HTTP 200 and secret data")
    void adminEndpointAllowsAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(content().string("Top Secret Enterprise Data"));
    }

    @Test
    @DisplayName("GET /api/unknown without authentication should return HTTP 401 Unauthorized")
    void unknownEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/unknown"))
                .andExpect(status().isUnauthorized());
    }
}
