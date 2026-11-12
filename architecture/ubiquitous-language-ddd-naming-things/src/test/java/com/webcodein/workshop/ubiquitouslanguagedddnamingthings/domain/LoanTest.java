package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LoanTest {

    @Test
    @DisplayName("Should create unapproved loan with valid due date")
    void createLoan() {
        UUID bookId = UUID.randomUUID();
        UUID patronId = UUID.randomUUID();
        LocalDate dueDate = LocalDate.now().plusDays(14);

        Loan loan = new Loan(bookId, patronId, dueDate);

        assertNotNull(loan.getId());
        assertEquals(bookId, loan.getBookId());
        assertEquals(patronId, loan.getPatronId());
        assertEquals(dueDate, loan.getDueDate());
        assertFalse(loan.isApproved());
    }

    @Test
    @DisplayName("Should approve loan using ubiquitous intention method")
    void approveLoan() {
        Loan loan = new Loan(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().plusDays(7));
        loan.approve();
        assertTrue(loan.isApproved());
    }

    @Test
    @DisplayName("Should reject loan using ubiquitous intention method")
    void rejectLoan() {
        Loan loan = new Loan(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().plusDays(7));
        loan.approve();
        loan.reject();
        assertFalse(loan.isApproved());
    }
}
