package com.webcodein.workshop.owasp.model;

import java.util.Objects;

/**
 * Strict DTO representing only the fields that clients are authorized to mutate.
 * Eliminates Mass Assignment (BOPLA) by design.
 */
public record UpdateAccountRequest(String email, String displayName) {

    public UpdateAccountRequest {
        Objects.requireNonNull(email, "Email must not be null");
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address format");
        }
        displayName = displayName != null ? displayName.trim() : "";
    }
}
