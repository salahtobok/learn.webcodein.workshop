package com.webcodein.workshop.csp.engine;

import com.webcodein.workshop.csp.model.CspDirective;
import com.webcodein.workshop.csp.model.CspSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CspPolicyBuilderTest {

    @Test
    @DisplayName("Should build strict CSP Level 3 policy with nonce and strict-dynamic")
    void shouldBuildStrictLevel3Policy() {
        String nonce = "r4nd0mN0nc3V4lu3";
        String policy = CspPolicyBuilder.strictLevel3Policy(nonce).build();

        assertThat(policy).contains("default-src 'self'");
        assertThat(policy).contains("script-src 'nonce-" + nonce + "' 'strict-dynamic' 'unsafe-inline' https:");
        assertThat(policy).contains("object-src 'none'");
        assertThat(policy).contains("base-uri 'none'");
        assertThat(policy).contains("form-action 'self'");
        assertThat(policy).contains("frame-ancestors 'none'");
    }

    @Test
    @DisplayName("Should build custom policy with directives and source expressions")
    void shouldBuildCustomPolicy() {
        CspPolicyBuilder builder = new CspPolicyBuilder()
                .addDirective(CspDirective.DEFAULT_SRC, CspSource.self())
                .addDirective(CspDirective.IMG_SRC, CspSource.self(), CspSource.host("https://images.example.com"))
                .addDirective(CspDirective.CONNECT_SRC, CspSource.self(), CspSource.host("https://api.example.com"));

        String policy = builder.build();
        assertThat(policy).contains("default-src 'self'");
        assertThat(policy).contains("img-src 'self' https://images.example.com");
        assertThat(policy).contains("connect-src 'self' https://api.example.com");
    }
}
