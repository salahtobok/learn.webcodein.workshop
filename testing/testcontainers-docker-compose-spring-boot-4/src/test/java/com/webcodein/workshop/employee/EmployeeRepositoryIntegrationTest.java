package com.webcodein.workshop.employee;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Testcontainers
class EmployeeRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    EmployeeRepository repository;

    @Test
    void shouldSaveAndRetrieveEmployee() {
        repository.save(new Employee("Ada"));

        assertThat(repository.findByName("Ada")).isPresent();
        assertThat(repository.findByName("Ada").get().getName()).isEqualTo("Ada");
    }

    @Test
    void shouldReturnEmptyWhenEmployeeNotFound() {
        assertThat(repository.findByName("NonExistent")).isEmpty();
    }
}
