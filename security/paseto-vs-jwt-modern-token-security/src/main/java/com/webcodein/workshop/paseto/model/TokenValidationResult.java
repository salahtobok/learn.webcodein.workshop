package com.webcodein.workshop.paseto.model;

/**
 * Result of validating an authentication token.
 */
public record TokenValidationResult(
    boolean valid,
    TokenClaims claims,
    String errorCode,
    String failureMessage
) {
    public static TokenValidationResult success(TokenClaims claims) {
        return new TokenValidationResult(true, claims, null, null);
    }

    public static TokenValidationResult failure(String errorCode, String message) {
        return new TokenValidationResult(false, null, errorCode, message);
    }
}
