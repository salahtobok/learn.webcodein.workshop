package com.webcodein.workshop.paseto.engine;

import com.webcodein.workshop.paseto.model.TokenClaims;
import com.webcodein.workshop.paseto.model.TokenValidationResult;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.security.Security;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PasetoV4SecurityEngineTest {

    @BeforeAll
    static void setupProvider() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }

    @Test
    @DisplayName("PASETO v4.public: Signs and successfully verifies authentic token")
    void shouldSignAndVerifyPublicToken() {
        PasetoV4SecurityEngine engine = new PasetoV4SecurityEngine();

        Ed25519KeyPairGenerator kpg = new Ed25519KeyPairGenerator();
        kpg.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
        AsymmetricCipherKeyPair keyPair = kpg.generateKeyPair();

        Instant now = Instant.now();
        TokenClaims claims = new TokenClaims("alice_99", "issuer", "aud", now, now.plusSeconds(600), Map.of("scope", "read:write"));

        String token = engine.signPublicToken(claims, (Ed25519PrivateKeyParameters) keyPair.getPrivate());
        assertThat(token).startsWith("v4.public.");

        TokenValidationResult result = engine.verifyPublicToken(token, (Ed25519PublicKeyParameters) keyPair.getPublic());
        assertThat(result.valid()).isTrue();
        assertThat(result.claims().subject()).isEqualTo("alice_99");
    }

    @Test
    @DisplayName("PASETO v4.public: Rejects token tampered with altered payload")
    void shouldRejectTamperedPublicToken() {
        PasetoV4SecurityEngine engine = new PasetoV4SecurityEngine();

        Ed25519KeyPairGenerator kpg = new Ed25519KeyPairGenerator();
        kpg.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
        AsymmetricCipherKeyPair keyPair = kpg.generateKeyPair();

        Instant now = Instant.now();
        TokenClaims claims = new TokenClaims("alice_99", "issuer", "aud", now, now.plusSeconds(600), Map.of("role", "USER"));

        String token = engine.signPublicToken(claims, (Ed25519PrivateKeyParameters) keyPair.getPrivate());

        // Tamper with one character in the token payload
        char[] chars = token.toCharArray();
        chars[chars.length - 10] = (chars[chars.length - 10] == 'A') ? 'B' : 'A';
        String tamperedToken = new String(chars);

        TokenValidationResult result = engine.verifyPublicToken(tamperedToken, (Ed25519PublicKeyParameters) keyPair.getPublic());
        assertThat(result.valid()).isFalse();
        assertThat(result.errorCode()).isEqualTo("SIGNATURE_VERIFICATION_FAILED");
    }

    @Test
    @DisplayName("PASETO v4.local: Encrypts and decrypts confidential claims with 32-byte secret")
    void shouldEncryptAndDecryptLocalToken() {
        PasetoV4SecurityEngine engine = new PasetoV4SecurityEngine();
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);

        Instant now = Instant.now();
        TokenClaims claims = new TokenClaims("bob_internal", "issuer", "aud", now, now.plusSeconds(600), Map.of("secret_key", "sk_live_999888"));

        String localToken = engine.encryptLocalToken(claims, key);
        assertThat(localToken).startsWith("v4.local.");

        TokenValidationResult result = engine.decryptLocalToken(localToken, key);
        assertThat(result.valid()).isTrue();
        assertThat(result.claims().subject()).isEqualTo("bob_internal");
        assertThat(result.claims().customClaims().get("secret_key")).isEqualTo("sk_live_999888");
    }

    @Test
    @DisplayName("PASETO v4.local: Rejects decryption when wrong key is provided")
    void shouldRejectWrongSymmetricKey() {
        PasetoV4SecurityEngine engine = new PasetoV4SecurityEngine();
        byte[] key1 = new byte[32];
        byte[] key2 = new byte[32];
        key1[0] = 1;
        key2[0] = 2;

        Instant now = Instant.now();
        TokenClaims claims = new TokenClaims("user", "iss", "aud", now, now.plusSeconds(600), null);

        String token = engine.encryptLocalToken(claims, key1);
        TokenValidationResult result = engine.decryptLocalToken(token, key2);

        assertThat(result.valid()).isFalse();
        assertThat(result.errorCode()).isEqualTo("DECRYPTION_FAILED");
    }
}
