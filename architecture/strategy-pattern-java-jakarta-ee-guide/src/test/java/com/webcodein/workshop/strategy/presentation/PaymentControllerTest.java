package com.webcodein.workshop.strategy.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.workshop.strategy.domain.PaymentRequest;
import com.webcodein.workshop.strategy.domain.PaymentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should process credit card payment successfully via POST /api/payments")
    void shouldProcessCreditCardPayment() throws Exception {
        PaymentRequest request = new PaymentRequest(
                new BigDecimal("100.00"),
                PaymentType.CREDIT_CARD,
                "CUST-123",
                "1234-5678-9012-3456"
        );

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Credit Card payment successful"));
    }

    @Test
    @DisplayName("Should process PayPal payment successfully via POST /api/payments")
    void shouldProcessPayPalPayment() throws Exception {
        PaymentRequest request = new PaymentRequest(
                new BigDecimal("50.00"),
                PaymentType.PAYPAL,
                "CUST-456",
                "user@example.com"
        );

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("PayPal transaction completed"));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when strategy is unsupported")
    void shouldReturnBadRequestForCrypto() throws Exception {
        // Crypto strategy is not implemented yet
        PaymentRequest request = new PaymentRequest(
                new BigDecimal("1.5"),
                PaymentType.CRYPTO,
                "CUST-789",
                "wallet-address"
        );

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No payment strategy found for type: CRYPTO"));
    }
}
