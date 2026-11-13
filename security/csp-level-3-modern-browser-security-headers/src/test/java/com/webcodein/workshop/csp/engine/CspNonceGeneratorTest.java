package com.webcodein.workshop.csp.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CspNonceGeneratorTest {

    private final CspNonceGenerator generator = new CspNonceGenerator();

    @Test
    @DisplayName("Should generate valid Base64 nonce with minimum 128-bit entropy")
    void shouldGenerateValidBase64Nonce() {
        String nonce = generator.generateNonce();
        assertThat(nonce).isNotBlank();

        byte[] decoded = Base64.getDecoder().decode(nonce);
        assertThat(decoded).hasSize(16); // 16 bytes = 128 bits
    }

    @Test
    @DisplayName("Should reject nonce size below 16 bytes")
    void shouldRejectNonceSizeBelow16Bytes() {
        assertThatThrownBy(() -> generator.generateNonce(8))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nonce length must be at least 16 bytes");
    }

    @Test
    @DisplayName("Should generate unique nonces per request")
    void shouldGenerateUniqueNonces() {
        Set<String> nonces = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            boolean added = nonces.add(generator.generateNonce());
            assertThat(added).isTrue();
        }
        assertThat(nonces).hasSize(100);
    }
}
