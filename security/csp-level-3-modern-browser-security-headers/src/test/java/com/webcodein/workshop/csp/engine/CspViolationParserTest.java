package com.webcodein.workshop.csp.engine;

import com.webcodein.workshop.csp.model.CspViolationReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class CspViolationParserTest {

    private final CspViolationParser parser = new CspViolationParser();

    @Test
    @DisplayName("Should parse legacy csp-report JSON envelope")
    void shouldParseLegacyCspReportEnvelope() throws IOException {
        String json = """
                {
                  "csp-report": {
                    "document-uri": "https://example.com/checkout",
                    "referrer": "https://google.com",
                    "violated-directive": "script-src",
                    "effective-directive": "script-src-elem",
                    "original-policy": "default-src 'self'; script-src 'nonce-xyz'",
                    "blocked-uri": "https://evil-attacker.com/malicious.js",
                    "status-code": 200
                  }
                }
                """;

        CspViolationReport report = parser.parseReport(json);
        assertThat(report.documentUri()).isEqualTo("https://example.com/checkout");
        assertThat(report.violatedDirective()).isEqualTo("script-src");
        assertThat(report.blockedUri()).isEqualTo("https://evil-attacker.com/malicious.js");
        assertThat(report.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should parse modern W3C Reporting API body format")
    void shouldParseModernReportingApiFormat() throws IOException {
        String json = """
                {
                  "type": "csp-violation",
                  "age": 10,
                  "url": "https://example.com/dashboard",
                  "body": {
                    "document-uri": "https://example.com/dashboard",
                    "effective-directive": "script-src-elem",
                    "blocked-uri": "inline",
                    "status-code": 200
                  }
                }
                """;

        CspViolationReport report = parser.parseReport(json);
        assertThat(report.documentUri()).isEqualTo("https://example.com/dashboard");
        assertThat(report.effectiveDirective()).isEqualTo("script-src-elem");
        assertThat(report.blockedUri()).isEqualTo("inline");
    }
}
