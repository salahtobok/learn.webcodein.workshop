package com.webcodein.security.zerotrust;

import com.webcodein.security.zerotrust.config.SecurityConfig.KeycloakGrantedAuthoritiesConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakGrantedAuthoritiesConverterTest {

    private final KeycloakGrantedAuthoritiesConverter converter = new KeycloakGrantedAuthoritiesConverter();

    @Test
    @DisplayName("Extracts roles from Keycloak realm_access claim and prefixes with ROLE_")
    void testExtractRealmAccessRoles() {
        Jwt jwt = createJwt(Map.of(
                "realm_access", Map.of("roles", List.of("USER", "ADMIN")),
                "scope", "read write"
        ));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        Set<String> authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertThat(authorityStrings).contains(
                "ROLE_USER",
                "ROLE_ADMIN",
                "SCOPE_read",
                "SCOPE_write"
        );
    }

    @Test
    @DisplayName("Extracts roles from custom top-level roles claim")
    void testExtractTopLevelRoles() {
        Jwt jwt = createJwt(Map.of(
                "roles", List.of("OPERATOR", "AUDITOR")
        ));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        Set<String> authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertThat(authorityStrings).contains("ROLE_OPERATOR", "ROLE_AUDITOR");
    }

    @Test
    @DisplayName("Handles JWT with missing realm_access and roles claims gracefully")
    void testEmptyClaims() {
        Jwt jwt = createJwt(Map.of("sub", "user-123"));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }

    private Jwt createJwt(Map<String, Object> claims) {
        return new Jwt(
                "mock-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                claims
        );
    }
}
