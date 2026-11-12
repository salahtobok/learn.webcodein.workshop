package com.webcodein.flyway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
class CustomerRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("Should execute all 4 Flyway migrations and verify migrated data")
    void shouldRunFlywayMigrationsAndVerifyDataIsMigrated() {
        List<Customer> customers = customerRepository.findAll();
        
        assertThat(customers).hasSizeGreaterThanOrEqualTo(2);
        
        Customer john = customers.stream()
                .filter(c -> c.getEmail().equals("john.doe@example.com"))
                .findFirst()
                .orElseThrow();
                
        assertThat(john.getFullName()).isEqualTo("John Doe");

        Customer jane = customers.stream()
                .filter(c -> c.getEmail().equals("jane.smith@example.com"))
                .findFirst()
                .orElseThrow();
                
        assertThat(jane.getFullName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should save new customer with full name in migrated schema")
    void shouldSaveNewCustomerWithOnlyFullName() {
        Customer newCustomer = new Customer("alice.wonder@example.com", "Alice Wonder");
        customerRepository.saveAndFlush(newCustomer);

        Customer saved = customerRepository.findById(newCustomer.getId()).orElseThrow();
        assertThat(saved.getFullName()).isEqualTo("Alice Wonder");
    }

    @Test
    @DisplayName("Should find customer by email")
    void shouldFindCustomerByEmail() {
        Customer customer = customerRepository.findByEmail("john.doe@example.com").orElseThrow();
        assertThat(customer.getFullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should update customer full name in place")
    void shouldUpdateCustomerFullName() {
        Customer custom = new Customer("update.tester@example.com", "Original Name");
        customerRepository.saveAndFlush(custom);

        custom.setFullName("Updated Name");
        customerRepository.saveAndFlush(custom);

        Customer updated = customerRepository.findById(custom.getId()).orElseThrow();
        assertThat(updated.getFullName()).isEqualTo("Updated Name");
    }
}
