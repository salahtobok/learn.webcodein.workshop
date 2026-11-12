package com.webcodein.workshop.strategy.infrastructure;

import com.webcodein.workshop.strategy.domain.PaymentRequest;
import com.webcodein.workshop.strategy.domain.PaymentResult;
import com.webcodein.workshop.strategy.domain.PaymentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CreditCardPaymentStrategyTest {

    private CreditCardPaymentStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new CreditCardPaymentStrategy();
    }

    @Test
    @DisplayName("Should support only CREDIT_CARD payment type")
    void shouldSupportOnlyCreditCard() {
        assertThat(strategy.supports(PaymentType.CREDIT_CARD)).isTrue();
        assertThat(strategy.supports(PaymentType.PAYPAL)).isFalse();
        assertThat(strategy.supports(PaymentType.CRYPTO)).isFalse();
    }

    @Test
    @DisplayName("Should process credit card payment and return approved result")
    void shouldProcessCreditCardPayment() {
        PaymentRequest request = new PaymentRequest(
                new BigDecimal("150.00"),
                PaymentType.CREDIT_CARD,
                "CUST-CC",
                "4111-XXXX-XXXX-1111"
        );

        PaymentResult result = strategy.process(request);

        assertThat(result.success()).isTrue();
        assertThat(result.transactionId()).isNotBlank();
        assertThat(result.message()).isEqualTo("Credit Card payment successful");
    }
}
