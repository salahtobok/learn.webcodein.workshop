package com.webcodein.workshop.mtls.engine;

import com.webcodein.workshop.mtls.model.CertificateIdentity;
import com.webcodein.workshop.mtls.model.MtlsAccessDecision;
import com.webcodein.workshop.mtls.model.SpiffeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MtlsAuthorizationPolicyTest {

    @Test
    @DisplayName("Should authorize caller when certificate is valid and service policy permits access")
    void shouldAuthorizeAllowedService() throws Exception {
        SpiffeCertificateAuthority ca = new SpiffeCertificateAuthority("test-mesh-ca.local");
        MtlsAuthorizationPolicy policy = new MtlsAuthorizationPolicy(ca.getCaCertificate(), "cluster.local");
        policy.allowServiceCommunication("payment-service", Set.of("order-service"));

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
        kpg.initialize(2048, new SecureRandom());
        KeyPair orderKey = kpg.generateKeyPair();

        SpiffeId orderSpiffe = new SpiffeId("cluster.local", "prod", "order-service");
        CertificateIdentity orderCert = ca.issueWorkloadCertificate(orderSpiffe, orderKey, Duration.ofHours(1));

        SpiffeId paymentTarget = new SpiffeId("cluster.local", "prod", "payment-service");
        MtlsAccessDecision decision = policy.authorize(orderCert, paymentTarget);

        assertThat(decision.allowed()).isTrue();
    }

    @Test
    @DisplayName("Should reject caller when trust domain does not match mesh configuration")
    void shouldRejectTrustDomainMismatch() throws Exception {
        SpiffeCertificateAuthority ca = new SpiffeCertificateAuthority("test-mesh-ca.local");
        MtlsAuthorizationPolicy policy = new MtlsAuthorizationPolicy(ca.getCaCertificate(), "cluster.local");

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
        kpg.initialize(2048, new SecureRandom());
        KeyPair foreignKey = kpg.generateKeyPair();

        SpiffeId foreignSpiffe = new SpiffeId("foreign.mesh", "prod", "rogue-service");
        CertificateIdentity foreignCert = ca.issueWorkloadCertificate(foreignSpiffe, foreignKey, Duration.ofHours(1));

        SpiffeId paymentTarget = new SpiffeId("cluster.local", "prod", "payment-service");
        MtlsAccessDecision decision = policy.authorize(foreignCert, paymentTarget);

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("Trust domain mismatch");
    }

    @Test
    @DisplayName("Should reject caller when service is not in the authorized caller list")
    void shouldRejectUnauthorizedService() throws Exception {
        SpiffeCertificateAuthority ca = new SpiffeCertificateAuthority("test-mesh-ca.local");
        MtlsAuthorizationPolicy policy = new MtlsAuthorizationPolicy(ca.getCaCertificate(), "cluster.local");
        policy.allowServiceCommunication("payment-service", Set.of("order-service"));

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
        kpg.initialize(2048, new SecureRandom());
        KeyPair frontendKey = kpg.generateKeyPair();

        SpiffeId frontendSpiffe = new SpiffeId("cluster.local", "prod", "frontend-gateway");
        CertificateIdentity frontendCert = ca.issueWorkloadCertificate(frontendSpiffe, frontendKey, Duration.ofHours(1));

        SpiffeId paymentTarget = new SpiffeId("cluster.local", "prod", "payment-service");
        MtlsAccessDecision decision = policy.authorize(frontendCert, paymentTarget);

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("Access Denied");
    }
}
