package com.webcodein.workshop.mtls.model;

import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Immutable SPIFFE ID representing workload identity in a Zero-Trust service mesh.
 * Format: spiffe://<trust-domain>/ns/<namespace>/sa/<service-account>
 */
public record SpiffeId(String trustDomain, String namespace, String serviceAccount) {

    private static final Pattern SPIFFE_PATTERN = Pattern.compile("^spiffe://([^/]+)/ns/([^/]+)/sa/([^/]+)$");

    public SpiffeId {
        Objects.requireNonNull(trustDomain, "Trust domain must not be null");
        Objects.requireNonNull(namespace, "Namespace must not be null");
        Objects.requireNonNull(serviceAccount, "Service account must not be null");
    }

    public static SpiffeId parse(String uriString) {
        Objects.requireNonNull(uriString, "URI string must not be null");
        Matcher matcher = SPIFFE_PATTERN.matcher(uriString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid SPIFFE ID format: " + uriString);
        }
        return new SpiffeId(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    public static SpiffeId parse(URI uri) {
        Objects.requireNonNull(uri, "URI must not be null");
        return parse(uri.toString());
    }

    public String toUriString() {
        return "spiffe://" + trustDomain + "/ns/" + namespace + "/sa/" + serviceAccount;
    }

    public URI toUri() {
        return URI.create(toUriString());
    }
}
