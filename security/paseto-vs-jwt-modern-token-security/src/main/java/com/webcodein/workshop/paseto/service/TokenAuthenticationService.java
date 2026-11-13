package com.webcodein.workshop.paseto.service;

import com.webcodein.workshop.paseto.engine.PasetoV4SecurityEngine;
import com.webcodein.workshop.paseto.model.TokenClaims;
import com.webcodein.workshop.paseto.model.TokenValidationResult;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.SecureRandom;
import java.security.Security;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Enterprise Authentication Service managing cryptographic token lifecycle via PASETO.
 */
public class TokenAuthenticationService {

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private final PasetoV4SecurityEngine engine;
    private final Ed25519PrivateKeyParameters signingKey;
    private final Ed25519PublicKeyParameters verificationKey;
    private final byte[] symmetricSecret;

    public TokenAuthenticationService() {
        this.engine = new PasetoV4SecurityEngine();

        // Generate Ed25519 keypair
        Ed25519KeyPairGenerator keyPairGen = new Ed25519KeyPairGenerator();
        keyPairGen.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
        AsymmetricCipherKeyPair keyPair = keyPairGen.generateKeyPair();
        this.signingKey = (Ed25519PrivateKeyParameters) keyPair.getPrivate();
        this.verificationKey = (Ed25519PublicKeyParameters) keyPair.getPublic();

        // Generate 32-byte symmetric secret
        this.symmetricSecret = new byte[32];
        new SecureRandom().nextBytes(this.symmetricSecret);
    }

    public String issuePublicAuthToken(String subject, Duration validityDuration, Map<String, Object> roles) {
        Objects.requireNonNull(subject, "Subject must not be null");
        Instant now = Instant.now();
        TokenClaims claims = new TokenClaims(
            subject,
            "https://learn.webcodein.com",
            "webcodein-api",
            now,
            now.plus(validityDuration),
            roles
        );
        return engine.signPublicToken(claims, signingKey);
    }

    public TokenValidationResult authenticatePublicToken(String token) {
        return engine.verifyPublicToken(token, verificationKey);
    }

    public String issueEncryptedLocalToken(String subject, Duration validityDuration, Map<String, Object> sensitiveData) {
        Objects.requireNonNull(subject, "Subject must not be null");
        Instant now = Instant.now();
        TokenClaims claims = new TokenClaims(
            subject,
            "https://learn.webcodein.com",
            "internal-service",
            now,
            now.plus(validityDuration),
            sensitiveData
        );
        return engine.encryptLocalToken(claims, symmetricSecret);
    }

    public TokenValidationResult authenticateLocalToken(String token) {
        return engine.decryptLocalToken(token, symmetricSecret);
    }

    public Ed25519PublicKeyParameters getVerificationKey() {
        return verificationKey;
    }
}
