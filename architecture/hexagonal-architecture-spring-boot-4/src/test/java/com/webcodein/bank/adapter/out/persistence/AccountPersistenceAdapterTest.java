package com.webcodein.bank.adapter.out.persistence;

import com.webcodein.bank.domain.model.Account;
import com.webcodein.bank.domain.model.AccountId;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AccountPersistenceAdapterTest {

    @Autowired
    private AccountPersistenceAdapter adapter;

    @Autowired
    private SpringDataAccountRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully load and map account from database")
    void shouldLoadAccountSuccessfully() {
        repository.save(new AccountJpaEntity(1L, new BigDecimal("250.00")));

        Account account = adapter.loadAccount(new AccountId(1L));

        assertThat(account).isNotNull();
        assertThat(account.getId().getValue()).isEqualTo(1L);
        assertThat(account.getBalance()).isEqualByComparingTo("250.00");
    }

    @Test
    @DisplayName("Should update existing account state in database")
    void shouldUpdateAccountSuccessfully() {
        repository.save(new AccountJpaEntity(2L, new BigDecimal("100.00")));

        Account accountToUpdate = new Account(new AccountId(2L), new BigDecimal("180.00"));
        adapter.update(accountToUpdate);

        AccountJpaEntity updatedEntity = repository.findById(2L).orElseThrow();
        assertThat(updatedEntity.getBalance()).isEqualByComparingTo("180.00");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when loading non-existent account")
    void shouldThrowWhenAccountNotFound() {
        assertThatThrownBy(() -> adapter.loadAccount(new AccountId(999L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Account not found with ID: 999");
    }
}
