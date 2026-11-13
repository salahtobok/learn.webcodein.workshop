package com.webcodein.workshop.oauth21.model;

import java.util.Objects;

public record TokenResponse(
        String accessToken,
        String tokenType,
        int expiresIn,
        String refreshToken,
        String scope
) {
    public TokenResponse {
        Objects.requireNonNull(accessToken, "accessToken must not be null");
        Objects.requireNonNull(tokenType, "tokenType must not be null");
    }
}
