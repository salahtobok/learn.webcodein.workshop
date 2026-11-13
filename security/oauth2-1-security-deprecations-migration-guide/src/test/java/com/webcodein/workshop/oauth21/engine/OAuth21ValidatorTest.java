package com.webcodein.workshop.oauth21.engine;

import com.webcodein.workshop.oauth21.model.AuthorizationRequest;
import com.webcodein.workshop.oauth21.model.PkceChallenge;
import com.webcodein.workshop.oauth21.model.TokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuth21ValidatorTest {

    private final OAuth21Validator validator = new OAuth21Validator();
    private final PkceGenerator pkceGenerator = new PkceGenerator();

    private final Set<String> registeredUris = Set.of(
            "https://app.example.com/oauth/callback",
            "https://portal.example.com/auth"
    );

    @Test
    @DisplayName("Should permit valid OAuth 2.1 authorization request with PKCE S256")
    void shouldPermitValidAuthorizationRequest() {
        PkceChallenge pkce = pkceGenerator.createChallenge();
        AuthorizationRequest req = new AuthorizationRequest(
                "code",
                "client-123",
                "https://app.example.com/oauth/callback",
                "openid profile",
                "state-xyz",
                pkce
        );

        assertThatCode(() -> validator.validateAuthorizationRequest(req, registeredUris))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should reject Implicit Grant response_type=token")
    void shouldRejectImplicitGrant() {
        PkceChallenge pkce = pkceGenerator.createChallenge();
        AuthorizationRequest req = new AuthorizationRequest(
                "token",
                "client-123",
                "https://app.example.com/oauth/callback",
                "openid",
                "state-xyz",
                pkce
        );

        assertThatThrownBy(() -> validator.validateAuthorizationRequest(req, registeredUris))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Implicit grant is deprecated and prohibited");
    }

    @Test
    @DisplayName("Should reject authorization request without PKCE")
    void shouldRejectAuthorizationRequestWithoutPkce() {
        AuthorizationRequest req = new AuthorizationRequest(
                "code",
                "client-123",
                "https://app.example.com/oauth/callback",
                "openid",
                "state-xyz",
                null
        );

        assertThatThrownBy(() -> validator.validateAuthorizationRequest(req, registeredUris))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PKCE code_challenge is mandatory");
    }

    @Test
    @DisplayName("Should reject wildcard in redirect URI")
    void shouldRejectWildcardInRedirectUri() {
        PkceChallenge pkce = pkceGenerator.createChallenge();
        AuthorizationRequest req = new AuthorizationRequest(
                "code",
                "client-123",
                "https://*.example.com/callback",
                "openid",
                "state-xyz",
                pkce
        );

        assertThatThrownBy(() -> validator.validateAuthorizationRequest(req, registeredUris))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Wildcards and path traversal in redirect_uri are strictly prohibited");
    }

    @Test
    @DisplayName("Should reject Resource Owner Password Credentials (ROPC)")
    void shouldRejectRopcGrant() {
        TokenRequest req = new TokenRequest(
                "password",
                null,
                null,
                "client-123",
                null,
                null
        );

        assertThatThrownBy(() -> validator.validateTokenRequest(req, "storedChallenge", "https://app.example.com/callback"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Resource Owner Password Credentials is deprecated and prohibited");
    }

    @Test
    @DisplayName("Should validate correct PKCE code_verifier redemption")
    void shouldValidateCorrectPkceRedemption() {
        PkceChallenge challenge = pkceGenerator.createChallenge();
        TokenRequest req = new TokenRequest(
                "authorization_code",
                "authz-code-123",
                "https://app.example.com/oauth/callback",
                "client-123",
                challenge.codeVerifier(),
                null
        );

        assertThatCode(() -> validator.validateTokenRequest(req, challenge.codeChallenge(), "https://app.example.com/oauth/callback"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should reject mismatched PKCE code_verifier")
    void shouldRejectMismatchedCodeVerifier() {
        PkceChallenge challenge = pkceGenerator.createChallenge();
        TokenRequest req = new TokenRequest(
                "authorization_code",
                "authz-code-123",
                "https://app.example.com/oauth/callback",
                "client-123",
                "wrong-verifier-which-does-not-hash-to-challenge-E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw",
                null
        );

        assertThatThrownBy(() -> validator.validateTokenRequest(req, challenge.codeChallenge(), "https://app.example.com/oauth/callback"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PKCE code_verifier does not match");
    }
}
