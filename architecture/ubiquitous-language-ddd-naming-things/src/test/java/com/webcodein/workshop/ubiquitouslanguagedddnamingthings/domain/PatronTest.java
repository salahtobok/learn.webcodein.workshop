package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PatronTest {

    @Test
    @DisplayName("Should create active patron with assigned profile")
    void createPatron() {
        PatronProfile profile = new PatronProfile("Jane Doe", "jane@library.org", "CARD-1001");
        Patron patron = new Patron(profile);

        assertNotNull(patron.getId());
        assertEquals(profile, patron.getProfile());
        assertTrue(patron.isActive());
    }

    @Test
    @DisplayName("Should allow active patron to borrow book")
    void activePatronCanBorrow() {
        Patron patron = new Patron(new PatronProfile("John Smith", "john@library.org", "CARD-1002"));
        Book book = new Book(new Isbn("978-0134685991"), "Effective Java");

        assertDoesNotThrow(() -> patron.borrow(book));
    }

    @Test
    @DisplayName("Should reject borrowing when patron account is suspended")
    void suspendedPatronCannotBorrow() {
        Patron patron = new Patron(new PatronProfile("John Smith", "john@library.org", "CARD-1002"));
        patron.suspendAccount();
        assertFalse(patron.isActive());

        Book book = new Book(new Isbn("978-0134685991"), "Effective Java");
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> patron.borrow(book));
        assertTrue(ex.getMessage().contains("Inactive patrons cannot borrow books"));
    }

    @Test
    @DisplayName("Should activate suspended patron account")
    void activateSuspendedPatron() {
        Patron patron = new Patron(new PatronProfile("Alice Brown", "alice@library.org", "CARD-1003"));
        patron.suspendAccount();
        assertFalse(patron.isActive());

        patron.activateAccount();
        assertTrue(patron.isActive());
    }

    @Test
    @DisplayName("Should accept paying fine with valid Money value object")
    void payFine() {
        Patron patron = new Patron(new PatronProfile("Bob White", "bob@library.org", "CARD-1004"));
        assertDoesNotThrow(() -> patron.payFine(new Money(BigDecimal.valueOf(5.00), "USD")));
    }
}
