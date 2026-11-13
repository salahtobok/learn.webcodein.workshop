package com.webcodein.workshop.tls.crypto;

import com.webcodein.workshop.tls.model.NamedGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;

import static org.assertj.core.api.Assertions.assertThat;

class KeyExchangeEngineTest {

    @Test
    @DisplayName("ECDH key exchange over SECP256R1 derives identical shared secret on both peers")
    void testSecp256r1KeyExchange() {
        KeyExchangeEngine.KeyExchangePair client = KeyExchangeEngine.generateKeyPair(NamedGroup.SECP256R1);
        KeyExchangeEngine.KeyExchangePair server = KeyExchangeEngine.generateKeyPair(NamedGroup.SECP256R1);

        byte[] clientSharedSecret = KeyExchangeEngine.computeSharedSecret(
                client.getPrivateKey(), server.getPublicKey(), NamedGroup.SECP256R1);
        byte[] serverSharedSecret = KeyExchangeEngine.computeSharedSecret(
                server.getPrivateKey(), client.getPublicKey(), NamedGroup.SECP256R1);

        assertThat(clientSharedSecret)
                .isNotEmpty()
                .isEqualTo(serverSharedSecret);
    }

    @Test
    @DisplayName("ECDH key exchange over SECP384R1 derives matching shared secret")
    void testSecp384r1KeyExchange() {
        KeyExchangeEngine.KeyExchangePair client = KeyExchangeEngine.generateKeyPair(NamedGroup.SECP384R1);
        KeyExchangeEngine.KeyExchangePair server = KeyExchangeEngine.generateKeyPair(NamedGroup.SECP384R1);

        byte[] clientSharedSecret = KeyExchangeEngine.computeSharedSecret(
                client.getPrivateKey(), server.getPublicKey(), NamedGroup.SECP384R1);
        byte[] serverSharedSecret = KeyExchangeEngine.computeSharedSecret(
                server.getPrivateKey(), client.getPublicKey(), NamedGroup.SECP384R1);

        assertThat(clientSharedSecret)
                .isNotEmpty()
                .isEqualTo(serverSharedSecret);
    }

    @Test
    @DisplayName("Key exchange over X25519 derives matching shared secret")
    void testX25519KeyExchange() {
        KeyExchangeEngine.KeyExchangePair client = KeyExchangeEngine.generateKeyPair(NamedGroup.X25519);
        KeyExchangeEngine.KeyExchangePair server = KeyExchangeEngine.generateKeyPair(NamedGroup.X25519);

        byte[] clientSharedSecret = KeyExchangeEngine.computeSharedSecret(
                client.getPrivateKey(), server.getPublicKey(), NamedGroup.X25519);
        byte[] serverSharedSecret = KeyExchangeEngine.computeSharedSecret(
                server.getPrivateKey(), client.getPublicKey(), NamedGroup.X25519);

        assertThat(clientSharedSecret)
                .isNotEmpty()
                .isEqualTo(serverSharedSecret);
    }

    @Test
    @DisplayName("Different peer key pairs generate different shared secrets")
    void testDifferentKeyPairsProduceDifferentSecrets() {
        KeyExchangeEngine.KeyExchangePair client = KeyExchangeEngine.generateKeyPair(NamedGroup.SECP256R1);
        KeyExchangeEngine.KeyExchangePair server1 = KeyExchangeEngine.generateKeyPair(NamedGroup.SECP256R1);
        KeyExchangeEngine.KeyExchangePair server2 = KeyExchangeEngine.generateKeyPair(NamedGroup.SECP256R1);

        byte[] secret1 = KeyExchangeEngine.computeSharedSecret(
                client.getPrivateKey(), server1.getPublicKey(), NamedGroup.SECP256R1);
        byte[] secret2 = KeyExchangeEngine.computeSharedSecret(
                client.getPrivateKey(), server2.getPublicKey(), NamedGroup.SECP256R1);

        assertThat(secret1).isNotEqualTo(secret2);
    }
}
