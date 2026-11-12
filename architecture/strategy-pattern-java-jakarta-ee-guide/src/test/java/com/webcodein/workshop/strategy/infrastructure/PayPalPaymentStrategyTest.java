package com.webcodein.workshop.strategy.infrastructure;

import com.webcodein.workshop.strategy.domain.PaymentRequest;
import com.webcodein.workshop.strategy.domain.PaymentResult;
import com.webcodein.workshop.strategy.domain.PaymentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PayPalPaymentStrategyTest {

    private PayPalPaymentStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PayPalPaymentStrategy();
    }

    @Test
    @DisplayName("Should support only PAYPAL payment type")
    void shouldSupportOnlyPayPal() {
        assertThat(strategy.supports(PaymentType.PAYPAL)).isTrue();
        assertThat(strategy.supports(PaymentType.CREDIT_CARD)).isFalse();
        assertThat(strategy.supports(PaymentType.CRYPTO)).isFalse();
    }

    @Test
    @DisplayName("Should process PayPal payment and return transaction prefixed with PP-")
    void shouldProcessPayPalPayment() {
        PaymentRequest request = new PaymentRequest(
                new BigDecimal("89.99"),
                PaymentType.PAYPAL,
                "CUST-PP",
                "customer@domain.com"
        );

        PaymentResult result = strategy.process(request);

        assertThat(result.success()).isTrue();
        assertThat(result.transactionId()).startsWith("PP-");
        assertThat(result.message()).isEqualTo("PayPal transaction completed");
    }
}
