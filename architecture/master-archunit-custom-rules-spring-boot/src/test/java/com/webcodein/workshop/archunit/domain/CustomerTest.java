package com.webcodein.workshop.archunit.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    @Test
    void shouldCreateCustomerWithValidName() {
        Customer customer = new Customer("Alice Smith");

        assertThat(customer.getName()).isEqualTo("Alice Smith");
        assertThat(customer.getCreatedAt()).isNotNull();
        assertThat(customer.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThatThrownBy(() -> new Customer(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer name cannot be blank");
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThatThrownBy(() -> new Customer("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer name cannot be blank");
    }
}
