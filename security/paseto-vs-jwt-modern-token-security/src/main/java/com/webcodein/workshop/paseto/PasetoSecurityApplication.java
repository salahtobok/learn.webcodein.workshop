package com.webcodein.workshop.paseto;

import com.webcodein.workshop.paseto.model.TokenValidationResult;
import com.webcodein.workshop.paseto.service.TokenAuthenticationService;

import java.time.Duration;
import java.util.Map;

/**
 * Console application demonstrating PASETO v4 token security in Java 25.
 */
public class PasetoSecurityApplication {

    public static void main(String[] args) {
        System.out.println("=== PASETO vs JWT Token Security Demonstration ===");
        TokenAuthenticationService authService = new TokenAuthenticationService();

        // 1. Issue Public Signed Token (v4.public)
        String publicToken = authService.issuePublicAuthToken(
            "user_enterprise_982",
            Duration.ofMinutes(15),
            Map.of("role", "SECURITY_ADMIN", "tier", "ENTERPRISE")
        );
        System.out.println("\nGenerated PASETO v4.public Token:");
        System.out.println(publicToken);

        TokenValidationResult publicResult = authService.authenticatePublicToken(publicToken);
        System.out.println("Public Verification Result: " + publicResult.valid());
        System.out.println("Authenticated Subject: " + publicResult.claims().subject());
        System.out.println("Claims: " + publicResult.claims().customClaims());

        // 2. Issue Encrypted Local Token (v4.local)
        String localToken = authService.issueEncryptedLocalToken(
            "session_internal_551",
            Duration.ofHours(1),
            Map.of("ssn_hash", "987-XX-1234", "bank_account", "ACC-789456")
        );
        System.out.println("\nGenerated PASETO v4.local Token (Encrypted):");
        System.out.println(localToken);

        TokenValidationResult localResult = authService.authenticateLocalToken(localToken);
        System.out.println("Local Decryption Result: " + localResult.valid());
        System.out.println("Decrypted Subject: " + localResult.claims().subject());
        System.out.println("Confidential Claims: " + localResult.claims().customClaims());
    }
}
