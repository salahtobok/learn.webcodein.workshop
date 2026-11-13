package com.webcodein.workshop.oauth21.engine;

import com.webcodein.workshop.oauth21.model.AuthorizationRequest;
import com.webcodein.workshop.oauth21.model.TokenRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.Set;

public class OAuth21Validator {

    private final PkceGenerator pkceGenerator = new PkceGenerator();

    public void validateAuthorizationRequest(AuthorizationRequest request, Set<String> registeredRedirectUris) {
        Objects.requireNonNull(request, "request must not be null");

        // Rule 1: Implicit Grant is strictly forbidden in OAuth 2.1
        if ("token".equalsIgnoreCase(request.responseType())) {
            throw new IllegalArgumentException("unsupported_response_type: Implicit grant is deprecated and prohibited in OAuth 2.1");
        }

        if (!"code".equalsIgnoreCase(request.responseType())) {
            throw new IllegalArgumentException("unsupported_response_type: Only authorization code flow is permitted");
        }

        // Rule 2: PKCE is MANDATORY for ALL clients in OAuth 2.1
        if (request.pkce() == null || request.pkce().codeChallenge() == null || request.pkce().codeChallenge().isBlank()) {
            throw new IllegalArgumentException("invalid_request: PKCE code_challenge is mandatory for all clients in OAuth 2.1");
        }

        if (!"S256".equals(request.pkce().codeChallengeMethod())) {
            throw new IllegalArgumentException("invalid_request: Only S256 code_challenge_method is permitted in OAuth 2.1");
        }

        // Rule 3: Exact string matching for redirect URIs (No wildcards, no path traversal)
        validateRedirectUri(request.redirectUri(), registeredRedirectUris);
    }

    public void validateTokenRequest(TokenRequest request, String storedChallenge, String expectedRedirectUri) {
        Objects.requireNonNull(request, "request must not be null");

        // Rule 4: Resource Owner Password Credentials (ROPC) is strictly forbidden in OAuth 2.1
        if ("password".equalsIgnoreCase(request.grantType())) {
            throw new IllegalArgumentException("unsupported_grant_type: Resource Owner Password Credentials is deprecated and prohibited in OAuth 2.1");
        }

        if (!"authorization_code".equalsIgnoreCase(request.grantType()) && !"refresh_token".equalsIgnoreCase(request.grantType()) && !"client_credentials".equalsIgnoreCase(request.grantType())) {
            throw new IllegalArgumentException("unsupported_grant_type: Unrecognized grant type");
        }

        if ("authorization_code".equalsIgnoreCase(request.grantType())) {
            if (request.codeVerifier() == null || request.codeVerifier().isBlank()) {
                throw new IllegalArgumentException("invalid_grant: code_verifier is required to redeem authorization code");
            }

            // Verify PKCE verifier against stored challenge
            String computedChallenge = pkceGenerator.computeCodeChallenge(request.codeVerifier());
            byte[] computedBytes = computedChallenge.getBytes(StandardCharsets.US_ASCII);
            byte[] storedBytes = storedChallenge.getBytes(StandardCharsets.US_ASCII);

            if (!MessageDigest.isEqual(computedBytes, storedBytes)) {
                throw new IllegalArgumentException("invalid_grant: PKCE code_verifier does not match registered code_challenge");
            }

            // Validate redirect URI matches exact authorization request URI
            if (request.redirectUri() != null && !request.redirectUri().equals(expectedRedirectUri)) {
                throw new IllegalArgumentException("invalid_grant: redirect_uri does not match original authorization request");
            }
        }
    }

    public void validateRedirectUri(String redirectUri, Set<String> registeredRedirectUris) {
        if (redirectUri.contains("*") || redirectUri.contains("..")) {
            throw new IllegalArgumentException("invalid_request_uri: Wildcards and path traversal in redirect_uri are strictly prohibited in OAuth 2.1");
        }

        if (registeredRedirectUris != null && !registeredRedirectUris.contains(redirectUri)) {
            throw new IllegalArgumentException("redirect_uri_mismatch: redirect_uri is not an exact match to a pre-registered client URI");
        }
    }
}
