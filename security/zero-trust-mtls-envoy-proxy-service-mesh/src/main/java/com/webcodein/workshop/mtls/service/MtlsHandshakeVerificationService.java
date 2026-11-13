package com.webcodein.workshop.mtls.service;

import com.webcodein.workshop.mtls.engine.MtlsAuthorizationPolicy;
import com.webcodein.workshop.mtls.engine.SpiffeCertificateAuthority;
import com.webcodein.workshop.mtls.model.CertificateIdentity;
import com.webcodein.workshop.mtls.model.MtlsAccessDecision;
import com.webcodein.workshop.mtls.model.SpiffeId;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Set;

/**
 * Enterprise service coordinating mTLS identity issuance and Zero-Trust mesh authorization.
 */
public class MtlsHandshakeVerificationService {

    private final SpiffeCertificateAuthority authority;
    private final MtlsAuthorizationPolicy policy;
    private final KeyPairGenerator kpg;

    public MtlsHandshakeVerificationService(String trustDomain) {
        this.authority = new SpiffeCertificateAuthority("root-mesh-ca." + trustDomain);
        this.policy = new MtlsAuthorizationPolicy(authority.getCaCertificate(), trustDomain);

        try {
            this.kpg = KeyPairGenerator.getInstance("RSA", "BC");
            this.kpg.initialize(2048, new SecureRandom());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize KeyPairGenerator", e);
        }

        // Configure default Zero-Trust mesh policies:
        // payment-service can only be called by order-service
        policy.allowServiceCommunication("payment-service", Set.of("order-service"));
        // order-service can be called by frontend-gateway
        policy.allowServiceCommunication("order-service", Set.of("frontend-gateway"));
    }

    public CertificateIdentity issueWorkloadIdentity(String trustDomain, String namespace, String serviceAccount) {
        SpiffeId spiffeId = new SpiffeId(trustDomain, namespace, serviceAccount);
        KeyPair kp = kpg.generateKeyPair();
        return authority.issueWorkloadCertificate(spiffeId, kp, Duration.ofHours(24));
    }

    public MtlsAccessDecision simulateMtlsCall(CertificateIdentity client, SpiffeId target) {
        return policy.authorize(client, target);
    }

    public SpiffeCertificateAuthority getAuthority() {
        return authority;
    }

    public MtlsAuthorizationPolicy getPolicy() {
        return policy;
    }
}
