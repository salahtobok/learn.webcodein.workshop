package com.webcodein.workshop.oauth21.model;

import java.util.Objects;

public record PkceChallenge(
        String codeVerifier,
        String codeChallenge,
        String codeChallengeMethod
) {
    public PkceChallenge {
        Objects.requireNonNull(codeChallenge, "codeChallenge must not be null");
        Objects.requireNonNull(codeChallengeMethod, "codeChallengeMethod must not be null");
        if (!"S256".equals(codeChallengeMethod)) {
            throw new IllegalArgumentException("OAuth 2.1 strictly requires S256 code challenge method (plain is deprecated and prohibited)");
        }
    }
}
