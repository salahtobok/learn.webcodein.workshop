package com.webcodein.workshop.mtls.model;

import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Objects;

/**
 * Validated X.509 certificate identity binding a workload SPIFFE ID.
 */
public record CertificateIdentity(
    X509Certificate certificate,
    SpiffeId spiffeId,
    Instant validFrom,
    Instant validTo
) {
    public CertificateIdentity {
        Objects.requireNonNull(certificate, "Certificate must not be null");
        Objects.requireNonNull(spiffeId, "SPIFFE ID must not be null");
        Objects.requireNonNull(validFrom, "ValidFrom must not be null");
        Objects.requireNonNull(validTo, "ValidTo must not be null");
    }

    public boolean isValidAt(Instant timestamp) {
        return !timestamp.isBefore(validFrom) && !timestamp.isAfter(validTo);
    }
}
