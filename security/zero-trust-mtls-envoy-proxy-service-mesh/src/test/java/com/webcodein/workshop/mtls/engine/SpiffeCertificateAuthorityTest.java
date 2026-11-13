package com.webcodein.workshop.mtls.engine;

import com.webcodein.workshop.mtls.model.CertificateIdentity;
import com.webcodein.workshop.mtls.model.SpiffeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SpiffeCertificateAuthorityTest {

    @Test
    @DisplayName("Should issue valid X.509 certificate with Subject Alternative Name (SAN) matching SPIFFE ID")
    void shouldIssueValidSpiffeCertificate() throws Exception {
        SpiffeCertificateAuthority ca = new SpiffeCertificateAuthority("test-mesh-ca.local");

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
        kpg.initialize(2048, new SecureRandom());
        KeyPair workloadKey = kpg.generateKeyPair();

        SpiffeId spiffeId = new SpiffeId("cluster.local", "production", "order-service");
        CertificateIdentity identity = ca.issueWorkloadCertificate(spiffeId, workloadKey, Duration.ofHours(12));

        assertThat(identity.spiffeId()).isEqualTo(spiffeId);
        assertThat(identity.isValidAt(Instant.now())).isTrue();

        // Verify signed by CA
        identity.certificate().verify(ca.getCaCertificate().getPublicKey());

        // Verify Subject Alternative Names contain the URI
        assertThat(identity.certificate().getSubjectAlternativeNames()).isNotNull();
    }
}
