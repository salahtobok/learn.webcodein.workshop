package com.webcodein.workshop.owasp.engine;

import com.webcodein.workshop.owasp.model.SecurityDecision;
import com.webcodein.workshop.owasp.model.UpdateAccountRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoplaInspectorTest {

    private final BoplaInspector inspector = new BoplaInspector();

    @Test
    @DisplayName("BOPLA: Should detect mass assignment attempt modifying sensitive internal properties")
    void shouldDetectMassAssignmentAttempt() {
        String json = "{\"email\":\"new@test.com\",\"balance\":\"9999999.00\"}";
        SecurityDecision decision = inspector.inspectRawJsonForMassAssignment(json);

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.violationType()).isEqualTo("BOPLA_MASS_ASSIGNMENT_DETECTED");
        assertThat(decision.reason()).contains("balance");
    }

    @Test
    @DisplayName("BOPLA: Should detect unrecognized injection parameters")
    void shouldDetectUnrecognizedProperties() {
        String json = "{\"email\":\"new@test.com\",\"maliciousPayload\":\"&lt;script&gt;\"}";
        SecurityDecision decision = inspector.inspectRawJsonForMassAssignment(json);

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.violationType()).isEqualTo("UNRECOGNIZED_PROPERTY");
    }

    @Test
    @DisplayName("Should successfully parse clean JSON adhering strictly to allowed schema")
    void shouldParseCleanJson() throws Exception {
        String json = "{\"email\":\"valid@test.com\",\"displayName\":\"Valid User\"}";
        UpdateAccountRequest request = inspector.parseStrictDto(json);

        assertThat(request.email()).isEqualTo("valid@test.com");
        assertThat(request.displayName()).isEqualTo("Valid User");
    }

    @Test
    @DisplayName("Should throw SecurityException when parsing untrusted JSON containing mass-assignment injection")
    void shouldThrowSecurityExceptionOnTampering() {
        String json = "{\"email\":\"attacker@test.com\",\"tier\":\"VIP_UNLIMITED\"}";

        assertThatThrownBy(() -> inspector.parseStrictDto(json))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("BOPLA Violation");
    }
}
