package com.webcodein.bank.configuration;

import com.webcodein.bank.domain.port.out.LoadAccountPort;
import com.webcodein.bank.domain.port.out.UpdateAccountStatePort;
import com.webcodein.bank.domain.service.TransferMoneyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public TransferMoneyService transferMoneyService(
            LoadAccountPort loadAccountPort,
            UpdateAccountStatePort updateAccountStatePort) {

        return new TransferMoneyService(loadAccountPort, updateAccountStatePort);
    }
}
