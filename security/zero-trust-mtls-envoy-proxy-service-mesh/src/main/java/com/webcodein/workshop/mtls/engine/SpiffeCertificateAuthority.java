package com.webcodein.workshop.mtls.engine;

import com.webcodein.workshop.mtls.model.CertificateIdentity;
import com.webcodein.workshop.mtls.model.SpiffeId;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * Generates Root CA certificates and issues SPIFFE-compliant X.509 SVIDs (SPIFFE Verifiable Identity Documents).
 */
public class SpiffeCertificateAuthority {

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private final KeyPair caKeyPair;
    private final X509Certificate caCertificate;
    private final SecureRandom secureRandom = new SecureRandom();

    public SpiffeCertificateAuthority(String caCommonName) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
            kpg.initialize(2048, secureRandom);
            this.caKeyPair = kpg.generateKeyPair();

            Instant now = Instant.now();
            Date notBefore = Date.from(now);
            Date notAfter = Date.from(now.plus(Duration.ofDays(365)));

            X500Name issuer = new X500Name("CN=" + caCommonName + ", O=WebCodein Mesh CA, C=US");
            BigInteger serial = BigInteger.valueOf(Math.abs(secureRandom.nextLong()));

            JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                issuer, serial, notBefore, notAfter, issuer, caKeyPair.getPublic()
            );
            builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

            ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(caKeyPair.getPrivate());
            X509CertificateHolder holder = builder.build(signer);
            this.caCertificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize SPIFFE Certificate Authority", e);
        }
    }

    /**
     * Issues an X.509 leaf certificate with Subject Alternative Name (SAN) containing the SPIFFE ID URI.
     */
    public CertificateIdentity issueWorkloadCertificate(SpiffeId spiffeId, KeyPair workloadKeyPair, Duration validity) {
        Objects.requireNonNull(spiffeId, "SPIFFE ID must not be null");
        Objects.requireNonNull(workloadKeyPair, "Workload KeyPair must not be null");

        try {
            Instant now = Instant.now();
            Date notBefore = Date.from(now.minusSeconds(60));
            Date notAfter = Date.from(now.plus(validity));

            X500Name subject = new X500Name("CN=" + spiffeId.serviceAccount() + ", OU=" + spiffeId.namespace() + ", O=WebCodein Mesh");
            BigInteger serial = BigInteger.valueOf(Math.abs(secureRandom.nextLong()));

            JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                new X500Name(caCertificate.getSubjectX500Principal().getName()),
                serial, notBefore, notAfter, subject, workloadKeyPair.getPublic()
            );

            // SAN: URI:spiffe://<trust-domain>/ns/<namespace>/sa/<service-account>
            GeneralName spiffeSan = new GeneralName(GeneralName.uniformResourceIdentifier, spiffeId.toUriString());
            GeneralNames subjectAltNames = new GeneralNames(spiffeSan);
            builder.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
            builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

            ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(caKeyPair.getPrivate());
            X509CertificateHolder holder = builder.build(signer);
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);

            return new CertificateIdentity(cert, spiffeId, now.minusSeconds(60), now.plus(validity));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to issue SPIFFE certificate for: " + spiffeId, e);
        }
    }

    public X509Certificate getCaCertificate() {
        return caCertificate;
    }
}
