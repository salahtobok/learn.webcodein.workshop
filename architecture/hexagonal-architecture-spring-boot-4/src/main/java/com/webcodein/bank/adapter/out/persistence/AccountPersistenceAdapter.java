package com.webcodein.bank.adapter.out.persistence;

import com.webcodein.bank.domain.model.Account;
import com.webcodein.bank.domain.model.AccountId;
import com.webcodein.bank.domain.port.out.LoadAccountPort;
import com.webcodein.bank.domain.port.out.UpdateAccountStatePort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AccountPersistenceAdapter implements LoadAccountPort, UpdateAccountStatePort {

    private final SpringDataAccountRepository repository;
    private final AccountMapper mapper;

    public AccountPersistenceAdapter(SpringDataAccountRepository repository, AccountMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Account loadAccount(AccountId id) {
        AccountJpaEntity entity = repository.findById(id.getValue())
            .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + id.getValue()));
        return mapper.mapToDomainEntity(entity);
    }

    @Override
    public void update(Account account) {
        AccountJpaEntity entity = mapper.mapToJpaEntity(account);
        repository.save(entity);
    }
}
