package com.webcodein.workshop.oauth21.engine;

import com.webcodein.workshop.oauth21.model.PkceChallenge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PkceGeneratorTest {

    private final PkceGenerator generator = new PkceGenerator();

    @Test
    @DisplayName("Should generate valid PKCE code_verifier with sufficient entropy")
    void shouldGenerateValidCodeVerifier() {
        String verifier = generator.generateCodeVerifier();
        assertThat(verifier).isNotBlank();
        assertThat(verifier.length()).isBetween(43, 128);
    }

    @Test
    @DisplayName("Should compute S256 code_challenge correctly")
    void shouldComputeS256CodeChallenge() {
        // RFC 7636 Appendix B test vector
        String verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
        String expectedChallenge = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM";

        String challenge = generator.computeCodeChallenge(verifier);
        assertThat(challenge).isEqualTo(expectedChallenge);
    }

    @Test
    @DisplayName("Should reject invalid verifier lengths")
    void shouldRejectInvalidVerifierLengths() {
        assertThatThrownBy(() -> generator.computeCodeChallenge("short"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 43 and 128 characters");
    }

    @Test
    @DisplayName("Should create complete challenge bundle")
    void shouldCreateCompleteChallengeBundle() {
        PkceChallenge challenge = generator.createChallenge();
        assertThat(challenge.codeVerifier()).isNotBlank();
        assertThat(challenge.codeChallenge()).isNotBlank();
        assertThat(challenge.codeChallengeMethod()).isEqualTo("S256");
    }
}
