package com.webcodein.workshop.mtls.engine;

import com.webcodein.workshop.mtls.model.CertificateIdentity;
import com.webcodein.workshop.mtls.model.MtlsAccessDecision;
import com.webcodein.workshop.mtls.model.SpiffeId;

import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Evaluates Zero-Trust service-to-service authorization rules based on SPIFFE identity.
 */
public class MtlsAuthorizationPolicy {

    private final X509Certificate trustedCaCert;
    private final String expectedTrustDomain;
    private final Map<String, Set<String>> allowedCallers = new ConcurrentHashMap<>();

    public MtlsAuthorizationPolicy(X509Certificate trustedCaCert, String expectedTrustDomain) {
        this.trustedCaCert = trustedCaCert;
        this.expectedTrustDomain = expectedTrustDomain;
    }

    public void allowServiceCommunication(String targetService, Set<String> authorizedCallerServices) {
        allowedCallers.put(targetService, Collections.unmodifiableSet(Set.copyOf(authorizedCallerServices)));
    }

    public MtlsAccessDecision authorize(CertificateIdentity clientIdentity, SpiffeId targetServiceId) {
        // 1. Verify certificate validity window
        if (!clientIdentity.isValidAt(Instant.now())) {
            return MtlsAccessDecision.deny("Client certificate is expired or not yet valid",
                clientIdentity.spiffeId().toUriString(), targetServiceId.toUriString());
        }

        // 2. Verify CA cryptographic signature
        try {
            clientIdentity.certificate().verify(trustedCaCert.getPublicKey());
        } catch (Exception e) {
            return MtlsAccessDecision.deny("Client certificate was not signed by the trusted Mesh CA",
                clientIdentity.spiffeId().toUriString(), targetServiceId.toUriString());
        }

        // 3. Verify Trust Domain match
        SpiffeId clientSpiffe = clientIdentity.spiffeId();
        if (!clientSpiffe.trustDomain().equals(expectedTrustDomain)) {
            return MtlsAccessDecision.deny("Trust domain mismatch: " + clientSpiffe.trustDomain(),
                clientSpiffe.toUriString(), targetServiceId.toUriString());
        }

        // 4. Verify Service Authorization Matrix
        Set<String> permittedCallers = allowedCallers.getOrDefault(targetServiceId.serviceAccount(), Set.of());
        if (!permittedCallers.contains(clientSpiffe.serviceAccount())) {
            return MtlsAccessDecision.deny(
                "Access Denied: Service '" + clientSpiffe.serviceAccount() + "' is not permitted to call '" + targetServiceId.serviceAccount() + "'",
                clientSpiffe.toUriString(), targetServiceId.toUriString()
            );
        }

        return MtlsAccessDecision.allow(clientSpiffe.toUriString(), targetServiceId.toUriString());
    }
}
