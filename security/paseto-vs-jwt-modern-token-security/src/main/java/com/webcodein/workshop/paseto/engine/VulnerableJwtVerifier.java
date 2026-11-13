package com.webcodein.workshop.paseto.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.workshop.paseto.model.TokenClaims;
import com.webcodein.workshop.paseto.model.TokenValidationResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;

/**
 * Demonstrates the classic cryptographic hazard of JSON Web Tokens (JOSE RFC 7519):
 * Trusting the token's unauthenticated header ("alg") to dictate the verification algorithm.
 */
public class VulnerableJwtVerifier {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PublicKey expectedRsaPublicKey;
    private final byte[] sharedHmacSecret;

    public VulnerableJwtVerifier(PublicKey expectedRsaPublicKey, byte[] sharedHmacSecret) {
        this.expectedRsaPublicKey = expectedRsaPublicKey;
        this.sharedHmacSecret = sharedHmacSecret;
    }

    /**
     * Vulnerable JWT verification routine that reads "alg" from header and switches behavior.
     * This allows:
     * 1. "none" algorithm attack: signature stripped completely.
     * 2. Key confusion attack: token signed with HMAC using the RSA public key bytes!
     */
    public TokenValidationResult verifyVulnerableJwt(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length < 2) {
                return TokenValidationResult.failure("MALFORMED_TOKEN", "Token does not contain required segments");
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode header = objectMapper.readTree(headerJson);
            String alg = header.path("alg").asText("none");

            // Flaw 1: Attacker sends "none" algorithm
            if ("none".equalsIgnoreCase(alg)) {
                // In naive implementations, signature check was skipped!
                JsonNode payload = objectMapper.readTree(payloadJson);
                TokenClaims claims = extractClaims(payload);
                return TokenValidationResult.success(claims);
            }

            // Flaw 2: Algorithm confusion (attacker changes RS256 to HS256)
            if ("HS256".equalsIgnoreCase(alg)) {
                // If the server was expecting RSA, but blindly verifies with HMAC using the public key as the secret:
                byte[] signingInput = (parts[0] + "." + parts[1]).getBytes(StandardCharsets.UTF_8);
                Mac hmac = Mac.getInstance("HmacSHA256");
                // Key confusion vulnerability: using RSA public key bytes as HMAC secret
                byte[] keyBytes = (expectedRsaPublicKey != null) ? expectedRsaPublicKey.getEncoded() : sharedHmacSecret;
                hmac.init(new SecretKeySpec(keyBytes, "HmacSHA256"));
                byte[] expectedSig = hmac.doFinal(signingInput);
                byte[] providedSig = Base64.getUrlDecoder().decode(parts[2]);

                if (java.security.MessageDigest.isEqual(expectedSig, providedSig)) {
                    JsonNode payload = objectMapper.readTree(payloadJson);
                    return TokenValidationResult.success(extractClaims(payload));
                }
            }

            return TokenValidationResult.failure("INVALID_SIGNATURE", "Signature verification failed");
        } catch (Exception e) {
            return TokenValidationResult.failure("VERIFICATION_ERROR", e.getMessage());
        }
    }

    private TokenClaims extractClaims(JsonNode payload) {
        String sub = payload.path("sub").asText("anonymous");
        String iss = payload.path("iss").asText("unknown");
        String aud = payload.path("aud").asText("unknown");
        Instant iat = Instant.ofEpochSecond(payload.path("iat").asLong(Instant.now().getEpochSecond()));
        Instant exp = Instant.ofEpochSecond(payload.path("exp").asLong(Instant.now().plusSeconds(3600).getEpochSecond()));
        return new TokenClaims(sub, iss, aud, iat, exp, null);
    }
}
