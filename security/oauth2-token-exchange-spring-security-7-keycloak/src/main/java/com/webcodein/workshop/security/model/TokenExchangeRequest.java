package com.webcodein.workshop.security.model;

import java.util.Objects;

public record TokenExchangeRequest(
        String grantType,
        String subjectToken,
        String subjectTokenType,
        String actorToken,
        String actorTokenType,
        String requestedTokenType,
        String audience,
        String scope
) {
    public static final String GRANT_TYPE_TOKEN_EXCHANGE = "urn:ietf:params:oauth:grant-type:token-exchange";
    public static final String TOKEN_TYPE_ACCESS_TOKEN = "urn:ietf:params:oauth:token-type:access_token";
    public static final String TOKEN_TYPE_JWT = "urn:ietf:params:oauth:token-type:jwt";

    public TokenExchangeRequest {
        Objects.requireNonNull(grantType, "grantType must not be null");
        Objects.requireNonNull(subjectToken, "subjectToken must not be null");
        Objects.requireNonNull(subjectTokenType, "subjectTokenType must not be null");
        if (!GRANT_TYPE_TOKEN_EXCHANGE.equals(grantType)) {
            throw new IllegalArgumentException("grantType must be " + GRANT_TYPE_TOKEN_EXCHANGE);
        }
        if (subjectToken.isBlank()) {
            throw new IllegalArgumentException("subjectToken must not be blank");
        }
        if (audience == null || audience.isBlank()) {
            audience = "default-service";
        }
        if (scope == null || scope.isBlank()) {
            scope = "read";
        }
    }
}
