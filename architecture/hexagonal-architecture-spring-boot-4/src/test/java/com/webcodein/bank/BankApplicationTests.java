package com.webcodein.bank;

import com.webcodein.bank.domain.model.Account;
import com.webcodein.bank.domain.model.AccountId;
import com.webcodein.bank.domain.port.out.LoadAccountPort;
import com.webcodein.bank.domain.port.out.UpdateAccountStatePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BankApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoadAccountPort loadAccountPort;

    @Autowired
    private UpdateAccountStatePort updateAccountStatePort;

    @Test
    @DisplayName("Application context should load successfully")
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(loadAccountPort).isNotNull();
        assertThat(updateAccountStatePort).isNotNull();
    }

    @Test
    @DisplayName("End-to-end transfer execution should update state through ports")
    void shouldExecuteEndToEndTransfer() throws Exception {
        AccountId sourceId = new AccountId(1L);
        AccountId targetId = new AccountId(2L);

        updateAccountStatePort.update(new Account(sourceId, new BigDecimal("500.00")));
        updateAccountStatePort.update(new Account(targetId, new BigDecimal("100.00")));

        String requestJson = """
                {
                    "source": 1,
                    "target": 2,
                    "amount": 150.00
                }
                """;

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Transfer completed successfully"));

        Account sourceAccount = loadAccountPort.loadAccount(sourceId);
        Account targetAccount = loadAccountPort.loadAccount(targetId);

        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("350.00");
        assertThat(targetAccount.getBalance()).isEqualByComparingTo("250.00");
    }
}
