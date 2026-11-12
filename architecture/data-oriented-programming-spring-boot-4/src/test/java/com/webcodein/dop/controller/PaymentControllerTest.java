package com.webcodein.dop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.dop.model.CreditCard;
import com.webcodein.dop.model.Crypto;
import com.webcodein.dop.model.PayPal;
import com.webcodein.dop.model.PaymentRequest;
import com.webcodein.dop.service.PaymentProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import(PaymentProcessingService.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldProcessCreditCardEndpoint() throws Exception {
        PaymentRequest request = new PaymentRequest(200.0, new CreditCard("9876543210984321", "11/29"));

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Processing credit card ending in 4321 for $200.0"));
    }

    @Test
    void shouldProcessPayPalEndpoint() throws Exception {
        PaymentRequest request = new PaymentRequest(75.0, new PayPal("customer@domain.com"));

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Processing PayPal payment for customer@domain.com for $75.0"));
    }

    @Test
    void shouldProcessBitcoinEndpoint() throws Exception {
        PaymentRequest request = new PaymentRequest(1000.0, new Crypto("bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh", "BTC"));

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Processing Bitcoin payment to bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh for $1000.0"));
    }

    @Test
    void shouldRejectInvalidJsonWithoutType() throws Exception {
        String invalidJson = """
                {
                    "amount": 100.0,
                    "method": {
                        "cardNumber": "1234567812345678"
                    }
                }
                """;

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
