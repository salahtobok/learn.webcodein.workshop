package com.webcodein.workshop.paseto.model;

import java.util.Objects;

/**
 * PASETO header defining the version and security purpose.
 * Format: "v4.local." or "v4.public."
 */
public record PasetoHeader(String version, String purpose) {

    public static final PasetoHeader V4_LOCAL = new PasetoHeader("v4", "local");
    public static final PasetoHeader V4_PUBLIC = new PasetoHeader("v4", "public");

    public PasetoHeader {
        Objects.requireNonNull(version, "Version must not be null");
        Objects.requireNonNull(purpose, "Purpose must not be null");
        if (!version.equals("v4")) {
            throw new IllegalArgumentException("Only PASETO v4 is supported in this implementation");
        }
        if (!purpose.equals("local") && !purpose.equals("public")) {
            throw new IllegalArgumentException("Purpose must be 'local' or 'public'");
        }
    }

    public String headerPrefix() {
        return version + "." + purpose + ".";
    }
}
