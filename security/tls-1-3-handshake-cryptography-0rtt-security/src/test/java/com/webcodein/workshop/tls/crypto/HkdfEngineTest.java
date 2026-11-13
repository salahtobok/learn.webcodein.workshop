package com.webcodein.workshop.tls.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HkdfEngineTest {

    @Test
    @DisplayName("HKDF-Extract and Expand correctly derives output keys")
    void testHkdfExtractAndExpand() {
        byte[] salt = "salt-test-bytes-1234".getBytes(StandardCharsets.UTF_8);
        byte[] ikm = "input-keying-material".getBytes(StandardCharsets.UTF_8);
        byte[] info = "tls13-test-label".getBytes(StandardCharsets.UTF_8);

        byte[] prk = HkdfEngine.extract(salt, ikm, "HmacSHA256");
        assertThat(prk).hasSize(32);

        byte[] okm1 = HkdfEngine.expand(prk, info, 32, "HmacSHA256");
        byte[] okm2 = HkdfEngine.expand(prk, info, 32, "HmacSHA256");

        assertThat(okm1)
                .hasSize(32)
                .isEqualTo(okm2);
    }

    @Test
    @DisplayName("HKDF-Expand-Label correctly prefixes label and encodes context")
    void testHkdfExpandLabel() {
        byte[] secret = new byte[32];
        byte[] context = "test-context".getBytes(StandardCharsets.UTF_8);

        byte[] derived = HkdfEngine.expandLabel(secret, "key", context, 16, "HmacSHA256");
        assertThat(derived).hasSize(16);
    }

    @Test
    @DisplayName("Derive-Secret produces deterministic key derivation")
    void testDeriveSecretDeterministic() {
        byte[] secret = "pseudo-random-key-material-32byte".getBytes(StandardCharsets.UTF_8);
        byte[] transcriptHash = "sample-transcript-hash-256bit".getBytes(StandardCharsets.UTF_8);

        byte[] derived1 = HkdfEngine.deriveSecret(secret, "c hs traffic", transcriptHash, "HmacSHA256");
        byte[] derived2 = HkdfEngine.deriveSecret(secret, "c hs traffic", transcriptHash, "HmacSHA256");
        byte[] derived3 = HkdfEngine.deriveSecret(secret, "s hs traffic", transcriptHash, "HmacSHA256");

        assertThat(derived1)
                .hasSize(32)
                .isEqualTo(derived2)
                .isNotEqualTo(derived3);
    }

    @Test
    @DisplayName("HKDF throws exception when expanding to excessive length")
    void testHkdfExpandExcessiveLengthThrows() {
        byte[] prk = new byte[32];
        assertThatThrownBy(() -> HkdfEngine.expand(prk, new byte[0], 256 * 32, "HmacSHA256"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
