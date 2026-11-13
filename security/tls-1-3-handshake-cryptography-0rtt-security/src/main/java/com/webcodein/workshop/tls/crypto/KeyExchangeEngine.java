package com.webcodein.workshop.tls.crypto;

import com.webcodein.workshop.tls.model.NamedGroup;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public final class KeyExchangeEngine {

    public record KeyExchangePair(KeyPair keyPair, NamedGroup group) {
        public PublicKey getPublicKey() {
            return keyPair.getPublic();
        }

        public PrivateKey getPrivateKey() {
            return keyPair.getPrivate();
        }
    }

    private KeyExchangeEngine() {}

    public static KeyExchangePair generateKeyPair(NamedGroup group) {
        try {
            KeyPairGenerator kpg;
            if (group == NamedGroup.X25519) {
                try {
                    kpg = KeyPairGenerator.getInstance("X25519");
                } catch (NoSuchAlgorithmException e) {
                    kpg = KeyPairGenerator.getInstance("XDH");
                }
            } else {
                kpg = KeyPairGenerator.getInstance("EC");
                ECGenParameterSpec ecSpec = new ECGenParameterSpec(group.getStandardName());
                kpg.initialize(ecSpec, new SecureRandom());
            }
            return new KeyExchangePair(kpg.generateKeyPair(), group);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to generate key pair for group " + group + ": " + e.getMessage(), e);
        }
    }

    public static byte[] computeSharedSecret(PrivateKey privateKey, PublicKey peerPublicKey, NamedGroup group) {
        try {
            KeyAgreement keyAgreement;
            if (group == NamedGroup.X25519) {
                try {
                    keyAgreement = KeyAgreement.getInstance("X25519");
                } catch (NoSuchAlgorithmException e) {
                    keyAgreement = KeyAgreement.getInstance("XDH");
                }
            } else {
                keyAgreement = KeyAgreement.getInstance("ECDH");
            }
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(peerPublicKey, true);
            return keyAgreement.generateSecret();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to compute shared secret for group " + group + ": " + e.getMessage(), e);
        }
    }
}
