package com.webcodein.workshop.archunit.service;

import com.webcodein.workshop.archunit.annotation.UseCase;
import com.webcodein.workshop.archunit.domain.Customer;
import com.webcodein.workshop.archunit.repository.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class CreateCustomerUseCase {

    private final CustomerRepository repository;

    public CreateCustomerUseCase(CustomerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Long create(String name) {
        Customer customer = new Customer(name);
        return repository.save(customer).getId();
    }
}
