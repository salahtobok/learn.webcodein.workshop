package com.webcodein.flyway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class CustomerRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldRunFlywayMigrationsAndVerifyDataIsMigrated() {
        // Flyway runs automatically on startup because of @DataJpaTest and spring.flyway.enabled=true
        // We know V1 inserted 2 legacy records, and V3 updated them.
        
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
    void shouldSaveNewCustomerWithOnlyFullName() {
        Customer newCustomer = new Customer("alice.wonder@example.com", "Alice Wonder");
        customerRepository.saveAndFlush(newCustomer);

        Customer saved = customerRepository.findById(newCustomer.getId()).orElseThrow();
        assertThat(saved.getFullName()).isEqualTo("Alice Wonder");
    }
}
