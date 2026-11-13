package com.webcodein.workshop.oauth21.model;

import java.util.Objects;

public record AuthorizationRequest(
        String responseType,
        String clientId,
        String redirectUri,
        String scope,
        String state,
        PkceChallenge pkce
) {
    public AuthorizationRequest {
        Objects.requireNonNull(responseType, "responseType must not be null");
        Objects.requireNonNull(clientId, "clientId must not be null");
        Objects.requireNonNull(redirectUri, "redirectUri must not be null");
    }
}
