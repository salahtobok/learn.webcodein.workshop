package com.webcodein.dop.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayPalTest {

    @Test
    void shouldCreateValidPayPalRecord() {
        PayPal payPal = new PayPal("user@example.com");

        assertThat(payPal.email()).isEqualTo("user@example.com");
    }

    @Test
    void shouldRejectInvalidEmailWithoutAtSign() {
        assertThatThrownBy(() -> new PayPal("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valid email is required");
    }

    @Test
    void shouldRejectNullEmail() {
        assertThatThrownBy(() -> new PayPal(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valid email is required");
    }
}
