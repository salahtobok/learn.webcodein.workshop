package com.webcodein.workshop.archunit.service;

import com.webcodein.workshop.archunit.annotation.UseCase;
import com.webcodein.workshop.archunit.domain.Customer;
import com.webcodein.workshop.archunit.repository.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@UseCase
public class GetCustomerUseCase {

    private final CustomerRepository repository;

    public GetCustomerUseCase(CustomerRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<Customer> getById(Long id) {
        return repository.findById(id);
    }
}
