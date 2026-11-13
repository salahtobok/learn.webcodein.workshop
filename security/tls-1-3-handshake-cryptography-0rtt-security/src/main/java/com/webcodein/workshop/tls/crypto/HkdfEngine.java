package com.webcodein.workshop.tls.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Standard RFC 5869 and RFC 8446 Section 7.1 HKDF (HMAC-based Extract-and-Expand Key Derivation Function).
 */
public final class HkdfEngine {

    private HkdfEngine() {}

    public static byte[] extract(byte[] salt, byte[] ikm, String hmacAlgorithm) {
        try {
            Mac mac = Mac.getInstance(hmacAlgorithm);
            int hashLen = mac.getMacLength();
            if (salt == null || salt.length == 0) {
                salt = new byte[hashLen];
            }
            mac.init(new SecretKeySpec(salt, hmacAlgorithm));
            return mac.doFinal(ikm);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to execute HKDF-Extract: " + e.getMessage(), e);
        }
    }

    public static byte[] expand(byte[] prk, byte[] info, int length, String hmacAlgorithm) {
        try {
            Mac mac = Mac.getInstance(hmacAlgorithm);
            int hashLen = mac.getMacLength();
            int n = (int) Math.ceil((double) length / hashLen);
            if (n > 255) {
                throw new IllegalArgumentException("Cannot expand to more than 255 * hashLen bytes");
            }

            mac.init(new SecretKeySpec(prk, hmacAlgorithm));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(length);
            byte[] t = new byte[0];

            for (int i = 1; i <= n; i++) {
                mac.reset();
                if (t.length > 0) {
                    mac.update(t);
                }
                if (info != null && info.length > 0) {
                    mac.update(info);
                }
                mac.update((byte) i);
                t = mac.doFinal();

                int bytesToWrite = Math.min(hashLen, length - outputStream.size());
                outputStream.write(t, 0, bytesToWrite);
            }

            return outputStream.toByteArray();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to execute HKDF-Expand: " + e.getMessage(), e);
        }
    }

    /**
     * RFC 8446 Section 7.1: HKDF-Expand-Label
     * HkdfLabel = struct {
     *     uint16 length;
     *     opaque label<7..255> = "tls13 " + Label;
     *     opaque context<0..255> = Context;
     * }
     */
    public static byte[] expandLabel(byte[] secret, String label, byte[] context, int length, String hmacAlgorithm) {
        byte[] fullLabel = ("tls13 " + label).getBytes(StandardCharsets.US_ASCII);
        if (context == null) {
            context = new byte[0];
        }

        ByteBuffer buffer = ByteBuffer.allocate(2 + 1 + fullLabel.length + 1 + context.length);
        buffer.putShort((short) length);
        buffer.put((byte) fullLabel.length);
        buffer.put(fullLabel);
        buffer.put((byte) context.length);
        buffer.put(context);

        return expand(secret, buffer.array(), length, hmacAlgorithm);
    }

    /**
     * RFC 8446 Derive-Secret(Secret, Label, Messages) = HKDF-Expand-Label(Secret, Label, Transcript-Hash(Messages), Hash.length)
     */
    public static byte[] deriveSecret(byte[] secret, String label, byte[] transcriptHash, String hmacAlgorithm) {
        try {
            Mac mac = Mac.getInstance(hmacAlgorithm);
            int hashLen = mac.getMacLength();
            return expandLabel(secret, label, transcriptHash, hashLen, hmacAlgorithm);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to derive secret: " + e.getMessage(), e);
        }
    }
}
