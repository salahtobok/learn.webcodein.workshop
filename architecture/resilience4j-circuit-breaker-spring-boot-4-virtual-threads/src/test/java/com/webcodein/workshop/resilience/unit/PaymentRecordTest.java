package com.webcodein.workshop.resilience.unit;

import com.webcodein.workshop.resilience.model.PaymentRequest;
import com.webcodein.workshop.resilience.model.PaymentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentRecordTest {

    @Test
    @DisplayName("Should instantiate valid PaymentRequest record")
    void shouldCreateValidPaymentRequest() {
        PaymentRequest request = new PaymentRequest("TX-100", "ACC-999", new BigDecimal("150.00"), "USD");

        assertThat(request.transactionId()).isEqualTo("TX-100");
        assertThat(request.accountNumber()).isEqualTo("ACC-999");
        assertThat(request.amount()).isEqualByComparingTo("150.00");
        assertThat(request.currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should throw exception when transaction ID is blank")
    void shouldRejectBlankTransactionId() {
        assertThatThrownBy(() -> new PaymentRequest("", "ACC-999", new BigDecimal("150.00"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction ID cannot be blank");
    }

    @Test
    @DisplayName("Should throw exception when amount is zero or negative")
    void shouldRejectNonPositiveAmount() {
        assertThatThrownBy(() -> new PaymentRequest("TX-100", "ACC-999", new BigDecimal("0.00"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be strictly positive");
    }

    @Test
    @DisplayName("Should construct degraded fallback PaymentResponse")
    void shouldConstructFallbackResponse() {
        PaymentResponse fallback = PaymentResponse.fallback("TX-200", new BigDecimal("50.00"), "EUR", "Remote timeout");

        assertThat(fallback.status()).isEqualTo("QUEUED_DEGRADED");
        assertThat(fallback.degradedFallback()).isTrue();
        assertThat(fallback.message()).contains("Gateway unavailable");
    }
}
