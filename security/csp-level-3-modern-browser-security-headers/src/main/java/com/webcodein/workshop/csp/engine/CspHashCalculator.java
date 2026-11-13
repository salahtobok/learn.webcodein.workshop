package com.webcodein.workshop.csp.engine;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public class CspHashCalculator {

    public enum HashAlgorithm {
        SHA256("SHA-256", "sha256"),
        SHA384("SHA-384", "sha384"),
        SHA512("SHA-512", "sha512");

        private final String jcaName;
        private final String cspPrefix;

        HashAlgorithm(String jcaName, String cspPrefix) {
            this.jcaName = jcaName;
            this.cspPrefix = cspPrefix;
        }

        public String jcaName() {
            return jcaName;
        }

        public String cspPrefix() {
            return cspPrefix;
        }
    }

    public String calculateHash(String scriptContent, HashAlgorithm algorithm) {
        Objects.requireNonNull(scriptContent, "scriptContent must not be null");
        Objects.requireNonNull(algorithm, "algorithm must not be null");

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm.jcaName());
            byte[] hashBytes = digest.digest(scriptContent.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algorithm unavailable: " + algorithm.jcaName(), e);
        }
    }

    public String formatCspHashSource(String scriptContent, HashAlgorithm algorithm) {
        String base64 = calculateHash(scriptContent, algorithm);
        return "'" + algorithm.cspPrefix() + "-" + base64 + "'";
    }
}
