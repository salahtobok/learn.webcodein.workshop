package com.webcodein.bank.adapter.in.web;

import com.webcodein.bank.domain.model.AccountId;
import com.webcodein.bank.domain.model.InsufficientFundsException;
import com.webcodein.bank.domain.port.in.TransferMoneyUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferMoneyUseCase transferMoneyUseCase;

    @Test
    @DisplayName("Should return 200 OK when transfer succeeds")
    void shouldReturn200WhenTransferSucceeds() throws Exception {
        String requestJson = """
                {
                    "source": 1,
                    "target": 2,
                    "amount": 75.50
                }
                """;

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Transfer completed successfully"));

        verify(transferMoneyUseCase).transfer(
                eq(new AccountId(1L)),
                eq(new AccountId(2L)),
                eq(new BigDecimal("75.50"))
        );
    }

    @Test
    @DisplayName("Should return 400 Bad Request when InsufficientFundsException is thrown")
    void shouldReturn400WhenInsufficientFunds() throws Exception {
        doThrow(new InsufficientFundsException("Cannot withdraw 500.00"))
                .when(transferMoneyUseCase)
                .transfer(any(AccountId.class), any(AccountId.class), any(BigDecimal.class));

        String requestJson = """
                {
                    "source": 1,
                    "target": 2,
                    "amount": 500.00
                }
                """;

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Cannot withdraw 500.00"));
    }
}
