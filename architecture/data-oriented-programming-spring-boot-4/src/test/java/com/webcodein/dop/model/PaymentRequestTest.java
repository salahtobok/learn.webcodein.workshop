package com.webcodein.dop.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentRequestTest {

    @Test
    void shouldCreateValidPaymentRequest() {
        PayPal payPal = new PayPal("buyer@store.com");
        PaymentRequest request = new PaymentRequest(250.0, payPal);

        assertThat(request.amount()).isEqualTo(250.0);
        assertThat(request.method()).isEqualTo(payPal);
    }

    @Test
    void shouldRejectZeroAmount() {
        PayPal payPal = new PayPal("buyer@store.com");
        assertThatThrownBy(() -> new PaymentRequest(0.0, payPal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment amount must be greater than zero");
    }

    @Test
    void shouldRejectNegativeAmount() {
        PayPal payPal = new PayPal("buyer@store.com");
        assertThatThrownBy(() -> new PaymentRequest(-50.0, payPal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment amount must be greater than zero");
    }

    @Test
    void shouldRejectNullPaymentMethod() {
        assertThatThrownBy(() -> new PaymentRequest(100.0, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment method must not be null");
    }
}
