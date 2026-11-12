package com.webcodein.security.zerotrust.controller;

import com.webcodein.security.zerotrust.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
@Import(SecurityConfig.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/public/hello returns 200 OK without authentication")
    void publicHello_ReturnsOkWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/public/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, this is a public endpoint."));
    }

    @Test
    @DisplayName("GET /api/secure/hello returns 401 Unauthorized when token is missing")
    void secureHello_ReturnsUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/secure/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/secure/hello returns 200 OK with authenticated JWT")
    void secureHello_ReturnsOkWithUserJwt() throws Exception {
        mockMvc.perform(get("/api/secure/hello")
                        .with(jwt().jwt(builder -> builder.claim("preferred_username", "alice"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello alice, you are authenticated via Zero-Trust OAuth2."));
    }

    @Test
    @DisplayName("GET /api/admin/dashboard returns 403 Forbidden for non-admin user")
    void adminDashboard_ReturnsForbiddenForUser() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/dashboard returns 200 OK for admin role")
    void adminDashboard_ReturnsOkForAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Welcome to the Admin Dashboard."));
    }
}
