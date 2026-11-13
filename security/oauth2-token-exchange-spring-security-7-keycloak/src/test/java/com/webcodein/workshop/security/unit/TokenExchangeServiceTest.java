package com.webcodein.workshop.security.unit;

import com.webcodein.workshop.security.model.SecurityAuditRecord;
import com.webcodein.workshop.security.model.TokenExchangeRequest;
import com.webcodein.workshop.security.model.TokenExchangeResponse;
import com.webcodein.workshop.security.service.TokenExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokenExchangeServiceTest {

    private TokenExchangeService service;

    @BeforeEach
    void setUp() {
        service = new TokenExchangeService();
    }

    @Test
    @DisplayName("Should exchange token in delegation mode with actor and subject claims")
    void shouldExchangeInDelegationMode() {
        TokenExchangeRequest request = new TokenExchangeRequest(
                TokenExchangeRequest.GRANT_TYPE_TOKEN_EXCHANGE,
                "token_alice_123",
                TokenExchangeRequest.TOKEN_TYPE_JWT,
                "token_gateway_456",
                TokenExchangeRequest.TOKEN_TYPE_JWT,
                TokenExchangeRequest.TOKEN_TYPE_ACCESS_TOKEN,
                "orders-service",
                "orders:create read"
        );

        TokenExchangeResponse response = service.exchangeToken(request);

        assertThat(response.accessToken()).startsWith("exchanged_jwt_");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(300);
        assertThat(response.scope()).contains("orders:create");

        List<SecurityAuditRecord> audits = service.getAllAuditRecords();
        assertThat(audits).hasSize(1);
        SecurityAuditRecord audit = audits.getFirst();
        assertThat(audit.subject()).isEqualTo("user:alice");
        assertThat(audit.actor()).isEqualTo("service:api-gateway");
        assertThat(audit.audience()).isEqualTo("orders-service");
    }

    @Test
    @DisplayName("Should downscope unapproved scopes during exchange")
    void shouldDownscopeUnapprovedScopes() {
        TokenExchangeRequest request = new TokenExchangeRequest(
                TokenExchangeRequest.GRANT_TYPE_TOKEN_EXCHANGE,
                "token_alice_123",
                TokenExchangeRequest.TOKEN_TYPE_JWT,
                null,
                null,
                null,
                "reports-service",
                "reports:read admin:delete"
        );

        TokenExchangeResponse response = service.exchangeToken(request);

        // admin:delete is not in allowed list, so it gets stripped
        assertThat(response.scope()).isEqualTo("reports:read");
    }
}
