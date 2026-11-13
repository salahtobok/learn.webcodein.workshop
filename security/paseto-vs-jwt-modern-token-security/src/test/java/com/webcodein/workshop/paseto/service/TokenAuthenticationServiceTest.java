package com.webcodein.workshop.paseto.service;

import com.webcodein.workshop.paseto.model.TokenValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TokenAuthenticationServiceTest {

    @Test
    @DisplayName("End-to-end token issuance and verification workflow for both public and local tokens")
    void shouldCompleteFullTokenLifecycle() {
        TokenAuthenticationService service = new TokenAuthenticationService();

        // Public Token
        String pubToken = service.issuePublicAuthToken("sec_lead", Duration.ofMinutes(30), Map.of("role", "ADMIN"));
        assertThat(pubToken).startsWith("v4.public.");

        TokenValidationResult pubResult = service.authenticatePublicToken(pubToken);
        assertThat(pubResult.valid()).isTrue();
        assertThat(pubResult.claims().subject()).isEqualTo("sec_lead");
        assertThat(pubResult.claims().customClaims().get("role")).isEqualTo("ADMIN");

        // Local Token
        String locToken = service.issueEncryptedLocalToken("tenant_42", Duration.ofHours(2), Map.of("db_schema", "tenant_42_vault"));
        assertThat(locToken).startsWith("v4.local.");

        TokenValidationResult locResult = service.authenticateLocalToken(locToken);
        assertThat(locResult.valid()).isTrue();
        assertThat(locResult.claims().subject()).isEqualTo("tenant_42");
        assertThat(locResult.claims().customClaims().get("db_schema")).isEqualTo("tenant_42_vault");
    }
}
