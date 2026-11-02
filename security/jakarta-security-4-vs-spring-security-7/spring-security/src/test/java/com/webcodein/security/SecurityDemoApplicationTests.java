package com.webcodein.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityDemoApplicationTests {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Spring Boot application context should load successfully with security configuration")
    void contextLoads() {
        assertNotNull(jwtDecoder);
    }
}
