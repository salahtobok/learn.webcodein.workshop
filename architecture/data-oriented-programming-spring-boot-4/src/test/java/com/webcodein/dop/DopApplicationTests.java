package com.webcodein.dop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.dop.model.CreditCard;
import com.webcodein.dop.model.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DopApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldProcessCreditCardPayment() throws Exception {
        PaymentRequest request = new PaymentRequest(150.0, new CreditCard("1234567890123456", "12/27"));

        // When using @JsonTypeInfo, Jackson will serialize it with "type": "creditCard"
        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Processing credit card ending in 3456 for $150.0"));
    }
}
