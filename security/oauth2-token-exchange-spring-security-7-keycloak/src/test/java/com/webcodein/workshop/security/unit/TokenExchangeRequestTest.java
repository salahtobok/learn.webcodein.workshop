package com.webcodein.workshop.security.unit;

import com.webcodein.workshop.security.model.TokenExchangeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenExchangeRequestTest {

    @Test
    @DisplayName("Should create valid RFC 8693 TokenExchangeRequest")
    void shouldCreateValidTokenExchangeRequest() {
        TokenExchangeRequest request = new TokenExchangeRequest(
                TokenExchangeRequest.GRANT_TYPE_TOKEN_EXCHANGE,
                "jwt_subject_token_alice",
                TokenExchangeRequest.TOKEN_TYPE_JWT,
                "jwt_actor_token_gateway",
                TokenExchangeRequest.TOKEN_TYPE_JWT,
                TokenExchangeRequest.TOKEN_TYPE_ACCESS_TOKEN,
                "payment-service",
                "payments:transfer"
        );

        assertThat(request.grantType()).isEqualTo(TokenExchangeRequest.GRANT_TYPE_TOKEN_EXCHANGE);
        assertThat(request.subjectToken()).isEqualTo("jwt_subject_token_alice");
        assertThat(request.actorToken()).isEqualTo("jwt_actor_token_gateway");
        assertThat(request.audience()).isEqualTo("payment-service");
        assertThat(request.scope()).isEqualTo("payments:transfer");
    }

    @Test
    @DisplayName("Should apply default audience and scope when omitted")
    void shouldApplyDefaultsWhenOmitted() {
        TokenExchangeRequest request = new TokenExchangeRequest(
                TokenExchangeRequest.GRANT_TYPE_TOKEN_EXCHANGE,
                "token_123",
                TokenExchangeRequest.TOKEN_TYPE_ACCESS_TOKEN,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(request.audience()).isEqualTo("default-service");
        assertThat(request.scope()).isEqualTo("read");
    }

    @Test
    @DisplayName("Should throw exception when grantType is invalid")
    void shouldThrowWhenGrantTypeIsInvalid() {
        assertThatThrownBy(() -> new TokenExchangeRequest(
                "password", "token", "type", null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("grantType must be urn:ietf:params:oauth:grant-type:token-exchange");
    }
}
