package com.webcodein.workshop.paseto.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.webcodein.workshop.paseto.model.PasetoHeader;
import com.webcodein.workshop.paseto.model.TokenClaims;
import com.webcodein.workshop.paseto.model.TokenValidationResult;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

/**
 * Modern Platform-Agnostic Security Tokens (PASETO Version 4) Engine.
 * Eliminates cipher negotiation, algorithm confusion, and 'none' attack hazards.
 */
public class PasetoV4SecurityEngine {

    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasetoV4SecurityEngine() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Signs claims into a PASETO v4.public token using an Ed25519 private key (RFC 8032).
     */
    public String signPublicToken(TokenClaims claims, Ed25519PrivateKeyParameters privateKey) {
        Objects.requireNonNull(claims, "Claims must not be null");
        Objects.requireNonNull(privateKey, "Private key must not be null");

        try {
            byte[] payloadBytes = objectMapper.writeValueAsBytes(claims);
            byte[] headerBytes = PasetoHeader.V4_PUBLIC.headerPrefix().getBytes(StandardCharsets.UTF_8);

            // Pre-Authentication Encoding (PAE) eliminates canonicalization attacks
            byte[] pae = preAuthEncode(headerBytes, payloadBytes, new byte[0]);

            Ed25519Signer signer = new Ed25519Signer();
            signer.init(true, privateKey);
            signer.update(pae, 0, pae.length);
            byte[] signature = signer.generateSignature();

            // v4.public.<payload + signature>
            byte[] body = new byte[payloadBytes.length + signature.length];
            System.arraycopy(payloadBytes, 0, body, 0, payloadBytes.length);
            System.arraycopy(signature, 0, body, payloadBytes.length, signature.length);

            return PasetoHeader.V4_PUBLIC.headerPrefix() + Base64.getUrlEncoder().withoutPadding().encodeToString(body);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign PASETO v4.public token", e);
        }
    }

    /**
     * Verifies a PASETO v4.public token using an Ed25519 public key.
     */
    public TokenValidationResult verifyPublicToken(String token, Ed25519PublicKeyParameters publicKey) {
        if (token == null || !token.startsWith(PasetoHeader.V4_PUBLIC.headerPrefix())) {
            return TokenValidationResult.failure("INVALID_HEADER", "Token header mismatch; expected v4.public.");
        }

        try {
            String b64Body = token.substring(PasetoHeader.V4_PUBLIC.headerPrefix().length());
            byte[] body = Base64.getUrlDecoder().decode(b64Body);

            if (body.length < 64) {
                return TokenValidationResult.failure("INVALID_PAYLOAD", "Payload too short for Ed25519 signature");
            }

            int payloadLen = body.length - 64;
            byte[] payloadBytes = new byte[payloadLen];
            byte[] signature = new byte[64];
            System.arraycopy(body, 0, payloadBytes, 0, payloadLen);
            System.arraycopy(body, payloadLen, signature, 0, 64);

            byte[] headerBytes = PasetoHeader.V4_PUBLIC.headerPrefix().getBytes(StandardCharsets.UTF_8);
            byte[] pae = preAuthEncode(headerBytes, payloadBytes, new byte[0]);

            Ed25519Signer verifier = new Ed25519Signer();
            verifier.init(false, publicKey);
            verifier.update(pae, 0, pae.length);

            if (!verifier.verifySignature(signature)) {
                return TokenValidationResult.failure("SIGNATURE_VERIFICATION_FAILED", "Ed25519 signature invalid");
            }

            TokenClaims claims = objectMapper.readValue(payloadBytes, TokenClaims.class);
            if (claims.isExpired(Instant.now())) {
                return TokenValidationResult.failure("TOKEN_EXPIRED", "Token has expired");
            }

            return TokenValidationResult.success(claims);
        } catch (Exception e) {
            return TokenValidationResult.failure("DECODE_ERROR", e.getMessage());
        }
    }

    /**
     * Encrypts claims into a PASETO v4.local token using authenticated symmetric encryption (ChaCha20-Poly1305).
     */
    public String encryptLocalToken(TokenClaims claims, byte[] symmetricKey32) {
        if (symmetricKey32 == null || symmetricKey32.length != 32) {
            throw new IllegalArgumentException("Symmetric key must be exactly 32 bytes");
        }

        try {
            byte[] payloadBytes = objectMapper.writeValueAsBytes(claims);
            byte[] nonce = new byte[12];
            secureRandom.nextBytes(nonce);

            byte[] headerBytes = PasetoHeader.V4_LOCAL.headerPrefix().getBytes(StandardCharsets.UTF_8);
            byte[] pae = preAuthEncode(headerBytes, nonce);

            Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(symmetricKey32, "ChaCha20-Poly1305"), new IvParameterSpec(nonce));
            cipher.updateAAD(pae);
            byte[] ciphertextWithTag = cipher.doFinal(payloadBytes);

            byte[] body = new byte[nonce.length + ciphertextWithTag.length];
            System.arraycopy(nonce, 0, body, 0, nonce.length);
            System.arraycopy(ciphertextWithTag, 0, body, nonce.length, ciphertextWithTag.length);

            return PasetoHeader.V4_LOCAL.headerPrefix() + Base64.getUrlEncoder().withoutPadding().encodeToString(body);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt PASETO v4.local token", e);
        }
    }

    /**
     * Decrypts and validates a PASETO v4.local token using authenticated symmetric encryption.
     */
    public TokenValidationResult decryptLocalToken(String token, byte[] symmetricKey32) {
        if (token == null || !token.startsWith(PasetoHeader.V4_LOCAL.headerPrefix())) {
            return TokenValidationResult.failure("INVALID_HEADER", "Token header mismatch; expected v4.local.");
        }

        try {
            String b64Body = token.substring(PasetoHeader.V4_LOCAL.headerPrefix().length());
            byte[] body = Base64.getUrlDecoder().decode(b64Body);

            if (body.length < 28) { // 12-byte nonce + minimum 16-byte Poly1305 tag
                return TokenValidationResult.failure("MALFORMED_LOCAL_PAYLOAD", "Payload too short for nonce and tag");
            }

            byte[] nonce = new byte[12];
            byte[] ciphertextWithTag = new byte[body.length - 12];
            System.arraycopy(body, 0, nonce, 0, 12);
            System.arraycopy(body, 12, ciphertextWithTag, 0, ciphertextWithTag.length);

            byte[] headerBytes = PasetoHeader.V4_LOCAL.headerPrefix().getBytes(StandardCharsets.UTF_8);
            byte[] pae = preAuthEncode(headerBytes, nonce);

            Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(symmetricKey32, "ChaCha20-Poly1305"), new IvParameterSpec(nonce));
            cipher.updateAAD(pae);
            byte[] decrypted = cipher.doFinal(ciphertextWithTag);

            TokenClaims claims = objectMapper.readValue(decrypted, TokenClaims.class);
            if (claims.isExpired(Instant.now())) {
                return TokenValidationResult.failure("TOKEN_EXPIRED", "Token has expired");
            }

            return TokenValidationResult.success(claims);
        } catch (Exception e) {
            return TokenValidationResult.failure("DECRYPTION_FAILED", e.getMessage());
        }
    }

    /**
     * Pre-Authentication Encoding (PAE) specified in RFC PASETO specification:
     * LE64(len(pieces)) || LE64(len(piece[0])) || piece[0] || ...
     */
    public static byte[] preAuthEncode(byte[]... pieces) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(le64(pieces.length));
        for (byte[] piece : pieces) {
            out.writeBytes(le64(piece.length));
            out.writeBytes(piece);
        }
        return out.toByteArray();
    }

    private static byte[] le64(long n) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(n);
        return buffer.array();
    }
}
