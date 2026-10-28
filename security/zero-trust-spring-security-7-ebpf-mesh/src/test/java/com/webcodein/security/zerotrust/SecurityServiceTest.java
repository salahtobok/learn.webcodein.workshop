package com.webcodein.security.zerotrust;

import com.webcodein.security.zerotrust.service.SecurityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityServiceTest {

    private final SecurityService securityService = new SecurityService();

    @Test
    @DisplayName("Returns true when JWT subject exactly matches accountId")
    void returnsTrueWhenSubjectMatchesAccountId() {
        Jwt jwt = createJwt("acc-100");
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);

        boolean isOwner = securityService.isOwner(auth, "acc-100");

        assertThat(isOwner).isTrue();
    }

    @Test
    @DisplayName("Returns false when JWT subject differs from accountId")
    void returnsFalseWhenSubjectDiffers() {
        Jwt jwt = createJwt("acc-100");
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);

        boolean isOwner = securityService.isOwner(auth, "acc-999");

        assertThat(isOwner).isFalse();
    }

    @Test
    @DisplayName("Returns false when authentication is null")
    void returnsFalseWhenAuthNull() {
        boolean isOwner = securityService.isOwner(null, "acc-100");

        assertThat(isOwner).isFalse();
    }

    @Test
    @DisplayName("Returns false when principal is not a Jwt")
    void returnsFalseWhenPrincipalNotJwt() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("user", "pass");

        boolean isOwner = securityService.isOwner(auth, "acc-100");

        assertThat(isOwner).isFalse();
    }

    private Jwt createJwt(String subject) {
        return new Jwt(
                "mock-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", subject)
        );
    }
}
