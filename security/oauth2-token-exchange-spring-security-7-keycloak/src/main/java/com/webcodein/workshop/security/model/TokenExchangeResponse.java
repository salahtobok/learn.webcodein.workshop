package com.webcodein.workshop.security.model;

import java.util.Objects;

public record TokenExchangeResponse(
        String accessToken,
        String issuedTokenType,
        String tokenType,
        int expiresIn,
        String scope
) {
    public TokenExchangeResponse {
        Objects.requireNonNull(accessToken, "accessToken must not be null");
        Objects.requireNonNull(issuedTokenType, "issuedTokenType must not be null");
        Objects.requireNonNull(tokenType, "tokenType must not be null");
        if (expiresIn <= 0) {
            throw new IllegalArgumentException("expiresIn must be positive");
        }
    }
}
