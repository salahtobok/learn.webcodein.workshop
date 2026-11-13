package com.webcodein.workshop.paseto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenClaimsTest {

    @Test
    @DisplayName("Should create valid TokenClaims and detect expiration correctly")
    void shouldCreateValidClaims() {
        Instant now = Instant.now();
        Instant future = now.plusSeconds(3600);
        TokenClaims claims = new TokenClaims("user123", "issuer", "audience", now, future, Map.of("role", "admin"));

        assertThat(claims.subject()).isEqualTo("user123");
        assertThat(claims.isExpired(now.plusSeconds(1800))).isFalse();
        assertThat(claims.isExpired(now.plusSeconds(4000))).isTrue();
    }

    @Test
    @DisplayName("Should reject claims where expiresAt is before issuedAt")
    void shouldRejectInvalidExpiration() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(60);

        assertThatThrownBy(() -> new TokenClaims("sub", "iss", "aud", now, past, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ExpiresAt");
    }
}
