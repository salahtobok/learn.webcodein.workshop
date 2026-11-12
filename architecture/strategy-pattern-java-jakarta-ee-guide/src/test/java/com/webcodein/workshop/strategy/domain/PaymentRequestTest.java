package com.webcodein.workshop.strategy.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentRequestTest {

    @Test
    @DisplayName("Should create PaymentRequest record and expose accessors accurately")
    void shouldCreatePaymentRequestRecord() {
        PaymentRequest request = new PaymentRequest(
                new BigDecimal("250.75"),
                PaymentType.CREDIT_CARD,
                "CUST-001",
                "4111-2222-3333-4444"
        );

        assertThat(request.amount()).isEqualByComparingTo(new BigDecimal("250.75"));
        assertThat(request.type()).isEqualTo(PaymentType.CREDIT_CARD);
        assertThat(request.customerId()).isEqualTo("CUST-001");
        assertThat(request.paymentDetails()).isEqualTo("4111-2222-3333-4444");
    }

    @Test
    @DisplayName("Should create PaymentResult record with success status and transaction ID")
    void shouldCreatePaymentResultRecord() {
        PaymentResult result = new PaymentResult(true, "TXN-9999", "Authorization approved");

        assertThat(result.success()).isTrue();
        assertThat(result.transactionId()).isEqualTo("TXN-9999");
        assertThat(result.message()).isEqualTo("Authorization approved");
    }
}
