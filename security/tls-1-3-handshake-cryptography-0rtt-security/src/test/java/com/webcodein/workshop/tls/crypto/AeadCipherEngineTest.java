package com.webcodein.workshop.tls.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AeadCipherEngineTest {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Test
    @DisplayName("AEAD AES-GCM encryption and decryption successfully recovers plaintext")
    void testAeadEncryptDecryptSuccess() {
        byte[] key = new byte[32]; // 256-bit key
        RANDOM.nextBytes(key);

        byte[] plaintext = "GET /api/v1/secure-data HTTP/1.1\r\nHost: example.com\r\n".getBytes(StandardCharsets.UTF_8);
        byte[] aad = "tls13-record-header".getBytes(StandardCharsets.UTF_8);

        byte[] ciphertext = AeadCipherEngine.encrypt(plaintext, key, aad);
        assertThat(ciphertext).isNotEqualTo(plaintext);

        byte[] decrypted = AeadCipherEngine.decrypt(ciphertext, key, aad);
        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("AEAD decryption fails when ciphertext is tampered with")
    void testAeadDecryptionFailsOnTampering() {
        byte[] key = new byte[16]; // 128-bit key
        RANDOM.nextBytes(key);

        byte[] plaintext = "Sensitive data".getBytes(StandardCharsets.UTF_8);
        byte[] ciphertext = AeadCipherEngine.encrypt(plaintext, key, null);

        // Tamper with last byte (part of authentication tag)
        ciphertext[ciphertext.length - 1] ^= 0x55;

        assertThatThrownBy(() -> AeadCipherEngine.decrypt(ciphertext, key, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("integrity check failed");
    }

    @Test
    @DisplayName("AEAD decryption fails when associated data does not match")
    void testAeadDecryptionFailsOnAadMismatch() {
        byte[] key = new byte[16];
        RANDOM.nextBytes(key);

        byte[] plaintext = "Authenticated data".getBytes(StandardCharsets.UTF_8);
        byte[] aad1 = "correct-header".getBytes(StandardCharsets.UTF_8);
        byte[] aad2 = "altered-header".getBytes(StandardCharsets.UTF_8);

        byte[] ciphertext = AeadCipherEngine.encrypt(plaintext, key, aad1);

        assertThatThrownBy(() -> AeadCipherEngine.decrypt(ciphertext, key, aad2))
                .isInstanceOf(IllegalStateException.class);
    }
}
