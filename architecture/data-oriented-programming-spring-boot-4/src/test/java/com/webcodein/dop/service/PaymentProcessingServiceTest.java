package com.webcodein.dop.service;

import com.webcodein.dop.model.CreditCard;
import com.webcodein.dop.model.Crypto;
import com.webcodein.dop.model.PayPal;
import com.webcodein.dop.model.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentProcessingServiceTest {

    private PaymentProcessingService service;

    @BeforeEach
    void setUp() {
        service = new PaymentProcessingService();
    }

    @Test
    void shouldProcessCreditCardPayment() {
        PaymentRequest request = new PaymentRequest(150.0, new CreditCard("1234567890123456", "12/28"));
        String result = service.processPayment(request);

        assertThat(result).isEqualTo("Processing credit card ending in 3456 for $150.0");
    }

    @Test
    void shouldProcessPayPalPayment() {
        PaymentRequest request = new PaymentRequest(85.5, new PayPal("shopper@example.com"));
        String result = service.processPayment(request);

        assertThat(result).isEqualTo("Processing PayPal payment for shopper@example.com for $85.5");
    }

    @Test
    void shouldProcessBitcoinWithSpecializedGuardClause() {
        PaymentRequest request = new PaymentRequest(500.0, new Crypto("bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq", "BTC"));
        String result = service.processPayment(request);

        assertThat(result).isEqualTo("Processing Bitcoin payment to bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq for $500.0");
    }

    @Test
    void shouldProcessEthereumWithGeneralCryptoPattern() {
        PaymentRequest request = new PaymentRequest(320.0, new Crypto("0x71C8FB9613335dc71A0", "ETH"));
        String result = service.processPayment(request);

        assertThat(result).isEqualTo("Processing Crypto payment in ETH to 0x71C8FB9613335dc71A0 for $320.0");
    }
}
