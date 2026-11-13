package com.webcodein.workshop.csp.model;

import java.util.Objects;

public record CspSource(String value) {

    public CspSource {
        Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }

    public static CspSource self() {
        return new CspSource("'self'");
    }

    public static CspSource none() {
        return new CspSource("'none'");
    }

    public static CspSource strictDynamic() {
        return new CspSource("'strict-dynamic'");
    }

    public static CspSource unsafeInline() {
        return new CspSource("'unsafe-inline'");
    }

    public static CspSource https() {
        return new CspSource("https:");
    }

    public static CspSource nonce(String base64Nonce) {
        Objects.requireNonNull(base64Nonce, "nonce must not be null");
        return new CspSource("'nonce-" + base64Nonce + "'");
    }

    public static CspSource hash(String algorithm, String base64Hash) {
        Objects.requireNonNull(algorithm, "algorithm must not be null");
        Objects.requireNonNull(base64Hash, "hash must not be null");
        return new CspSource("'" + algorithm.toLowerCase() + "-" + base64Hash + "'");
    }

    public static CspSource host(String host) {
        return new CspSource(host);
    }
}
