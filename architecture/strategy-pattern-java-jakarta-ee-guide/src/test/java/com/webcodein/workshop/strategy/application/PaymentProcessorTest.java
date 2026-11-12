package com.webcodein.workshop.strategy.application;

import com.webcodein.workshop.strategy.domain.PaymentRequest;
import com.webcodein.workshop.strategy.domain.PaymentResult;
import com.webcodein.workshop.strategy.domain.PaymentStrategy;
import com.webcodein.workshop.strategy.domain.PaymentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {

    @Mock
    private PaymentStrategy creditCardStrategy;

    @Mock
    private PaymentStrategy payPalStrategy;

    @Test
    @DisplayName("Should route to the matching strategy when supported")
    void shouldRouteToMatchingStrategy() {
        PaymentRequest request = new PaymentRequest(
                new BigDecimal("200.00"),
                PaymentType.CREDIT_CARD,
                "CUST-001",
                "4111-2222-3333-4444"
        );
        PaymentResult expectedResult = new PaymentResult(true, "TXN-123", "Success");

        when(creditCardStrategy.supports(PaymentType.CREDIT_CARD)).thenReturn(true);
        when(creditCardStrategy.process(request)).thenReturn(expectedResult);

        PaymentProcessor processor = new PaymentProcessor(List.of(payPalStrategy, creditCardStrategy));
        PaymentResult actualResult = processor.processPayment(request);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(creditCardStrategy).process(request);
        verify(payPalStrategy, never()).process(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when no strategy supports the payment type")
    void shouldThrowExceptionWhenTypeUnsupported() {
        PaymentRequest request = new PaymentRequest(
                new BigDecimal("500.00"),
                PaymentType.CRYPTO,
                "CUST-002",
                "0x71C...B29"
        );

        when(creditCardStrategy.supports(PaymentType.CRYPTO)).thenReturn(false);
        when(payPalStrategy.supports(PaymentType.CRYPTO)).thenReturn(false);

        PaymentProcessor processor = new PaymentProcessor(List.of(creditCardStrategy, payPalStrategy));

        assertThatThrownBy(() -> processor.processPayment(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No payment strategy found for type: CRYPTO");

        verify(creditCardStrategy, never()).process(any());
        verify(payPalStrategy, never()).process(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when strategy list is empty")
    void shouldThrowExceptionWhenStrategyListEmpty() {
        PaymentRequest request = new PaymentRequest(
                new BigDecimal("50.00"),
                PaymentType.PAYPAL,
                "CUST-003",
                "user@example.com"
        );

        PaymentProcessor processor = new PaymentProcessor(List.of());

        assertThatThrownBy(() -> processor.processPayment(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No payment strategy found for type: PAYPAL");
    }
}
