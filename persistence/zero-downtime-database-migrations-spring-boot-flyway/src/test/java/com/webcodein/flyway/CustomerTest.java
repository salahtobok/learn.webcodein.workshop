package com.webcodein.flyway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    @Test
    @DisplayName("Should create customer with valid email and full name")
    void shouldCreateCustomer() {
        Customer customer = new Customer("test@example.com", "John Doe");
        assertThat(customer.getEmail()).isEqualTo("test@example.com");
        assertThat(customer.getFullName()).isEqualTo("John Doe");
        assertThat(customer.getId()).isNull();
    }

    @Test
    @DisplayName("Should throw exception when email is null or blank")
    void shouldRejectInvalidEmail() {
        assertThatThrownBy(() -> new Customer(null, "John Doe"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be null or blank");

        assertThatThrownBy(() -> new Customer("   ", "John Doe"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw exception when full name is null or blank")
    void shouldRejectInvalidFullName() {
        assertThatThrownBy(() -> new Customer("test@example.com", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Full name cannot be null or blank");

        assertThatThrownBy(() -> new Customer("test@example.com", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Full name cannot be null or blank");
    }

    @Test
    @DisplayName("Should update full name when valid value provided")
    void shouldUpdateFullName() {
        Customer customer = new Customer("test@example.com", "John Doe");
        customer.setFullName("John Updated");
        assertThat(customer.getFullName()).isEqualTo("John Updated");
    }

    @Test
    @DisplayName("Should reject invalid full name during setter update")
    void shouldRejectBlankUpdate() {
        Customer customer = new Customer("test@example.com", "John Doe");
        assertThatThrownBy(() -> customer.setFullName(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Full name cannot be null or blank");
    }
}
