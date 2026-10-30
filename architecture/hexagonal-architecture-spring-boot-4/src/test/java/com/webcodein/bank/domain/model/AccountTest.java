package com.webcodein.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    @DisplayName("Should successfully deposit money into account")
    void shouldDepositMoney() {
        Account account = new Account(new AccountId(1L), new BigDecimal("100.00"));

        account.deposit(new BigDecimal("50.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("Should successfully withdraw money when sufficient balance exists")
    void shouldWithdrawMoneyWhenSufficientBalance() {
        Account account = new Account(new AccountId(1L), new BigDecimal("100.00"));

        account.withdraw(new BigDecimal("40.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException when withdrawing more than current balance")
    void shouldThrowWhenWithdrawingMoreThanBalance() {
        Account account = new Account(new AccountId(1L), new BigDecimal("20.00"));

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("50.00")))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Cannot withdraw 50.00");
    }
}
