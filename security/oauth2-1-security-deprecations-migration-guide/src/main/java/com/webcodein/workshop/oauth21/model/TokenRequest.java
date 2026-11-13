package com.webcodein.workshop.oauth21.model;

import java.util.Objects;

public record TokenRequest(
        String grantType,
        String code,
        String redirectUri,
        String clientId,
        String codeVerifier,
        String dpopProof
) {
    public TokenRequest {
        Objects.requireNonNull(grantType, "grantType must not be null");
    }
}
