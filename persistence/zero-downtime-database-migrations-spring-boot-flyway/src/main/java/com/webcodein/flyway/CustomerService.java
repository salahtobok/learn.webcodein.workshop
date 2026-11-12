package com.webcodein.flyway;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(CustomerRequest request) {
        Customer customer = new Customer(request.email(), request.fullName());
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomerName(Long id, String newFullName) {
        Customer customer = getCustomer(id);
        customer.setFullName(newFullName);
        return customerRepository.save(customer);
    }
}
