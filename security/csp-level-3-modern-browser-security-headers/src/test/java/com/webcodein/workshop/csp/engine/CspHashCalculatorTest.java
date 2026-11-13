package com.webcodein.workshop.csp.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CspHashCalculatorTest {

    private final CspHashCalculator calculator = new CspHashCalculator();

    @Test
    @DisplayName("Should calculate SHA-256 hash for inline script snippet")
    void shouldCalculateSha256Hash() {
        String script = "console.log('hello world');";
        String hash = calculator.calculateHash(script, CspHashCalculator.HashAlgorithm.SHA256);
        assertThat(hash).isNotBlank();

        String formatted = calculator.formatCspHashSource(script, CspHashCalculator.HashAlgorithm.SHA256);
        assertThat(formatted).startsWith("'sha256-").endsWith("'");
    }

    @Test
    @DisplayName("Should reject null script or algorithm")
    void shouldRejectNullScriptOrAlgorithm() {
        assertThatThrownBy(() -> calculator.calculateHash(null, CspHashCalculator.HashAlgorithm.SHA256))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> calculator.calculateHash("test", null))
                .isInstanceOf(NullPointerException.class);
    }
}
