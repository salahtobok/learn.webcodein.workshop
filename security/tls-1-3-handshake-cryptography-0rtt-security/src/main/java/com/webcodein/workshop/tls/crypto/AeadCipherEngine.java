package com.webcodein.workshop.tls.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

public final class AeadCipherEngine {

    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int GCM_NONCE_LENGTH_BYTES = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private AeadCipherEngine() {}

    public static byte[] encrypt(byte[] plaintext, byte[] key, byte[] associatedData) {
        try {
            byte[] nonce = new byte[GCM_NONCE_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(nonce);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            if (associatedData != null && associatedData.length > 0) {
                cipher.updateAAD(associatedData);
            }

            byte[] ciphertext = cipher.doFinal(plaintext);

            // Prepend nonce to ciphertext: [12-byte nonce | ciphertext + tag]
            byte[] result = new byte[nonce.length + ciphertext.length];
            System.arraycopy(nonce, 0, result, 0, nonce.length);
            System.arraycopy(ciphertext, 0, result, nonce.length, ciphertext.length);
            return result;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("AEAD Encryption failed: " + e.getMessage(), e);
        }
    }

    public static byte[] decrypt(byte[] payloadWithNonce, byte[] key, byte[] associatedData) {
        if (payloadWithNonce.length < GCM_NONCE_LENGTH_BYTES) {
            throw new IllegalArgumentException("Payload too short for GCM nonce");
        }
        try {
            byte[] nonce = Arrays.copyOfRange(payloadWithNonce, 0, GCM_NONCE_LENGTH_BYTES);
            byte[] ciphertext = Arrays.copyOfRange(payloadWithNonce, GCM_NONCE_LENGTH_BYTES, payloadWithNonce.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            if (associatedData != null && associatedData.length > 0) {
                cipher.updateAAD(associatedData);
            }

            return cipher.doFinal(ciphertext);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("AEAD Decryption failed (integrity check failed): " + e.getMessage(), e);
        }
    }
}
