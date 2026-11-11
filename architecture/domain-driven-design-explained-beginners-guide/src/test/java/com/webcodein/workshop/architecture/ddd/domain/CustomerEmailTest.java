package com.webcodein.workshop.architecture.ddd.domain;

import com.webcodein.workshop.architecture.ddd.domain.model.order.CustomerEmail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DDD Value Object: CustomerEmail Tests")
class CustomerEmailTest {

    @Test
    @DisplayName("Should accept valid customer email address and normalize to lowercase")
    void shouldAcceptValidEmail() {
        CustomerEmail email = new CustomerEmail("John.Doe@Example.COM");
        assertThat(email.value()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should reject invalid email formats")
    void shouldRejectInvalidEmail() {
        assertThatThrownBy(() -> new CustomerEmail("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid customer email format");

        assertThatThrownBy(() -> new CustomerEmail("user@"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new CustomerEmail("@domain.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should reject null email")
    void shouldRejectNullEmail() {
        assertThatThrownBy(() -> new CustomerEmail(null))
                .isInstanceOf(NullPointerException.class);
    }
}
