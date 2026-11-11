package com.webcodein.workshop.ddd.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DDD Pattern: Email Value Object Tests")
class EmailTest {

    @Test
    @DisplayName("Should create valid Email and normalize to lowercase")
    void shouldCreateValidEmail() {
        Email email = new Email("User.Name@Example.COM");
        assertEquals("user.name@example.com", email.value());
    }

    @Test
    @DisplayName("Should reject empty or blank email")
    void shouldRejectEmptyEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
        assertThrows(IllegalArgumentException.class, () -> new Email("   "));
    }

    @Test
    @DisplayName("Should reject invalid email format")
    void shouldRejectInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> new Email("noatsign.com"));
        assertThrows(IllegalArgumentException.class, () -> new Email("user@nodot"));
    }

    @Test
    @DisplayName("Should test value equality")
    void shouldTestEquality() {
        Email e1 = new Email("test@domain.com");
        Email e2 = new Email("TEST@domain.com");
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }
}
