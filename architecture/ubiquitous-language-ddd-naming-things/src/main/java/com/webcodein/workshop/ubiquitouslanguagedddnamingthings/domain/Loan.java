package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import java.util.UUID;
import java.time.LocalDate;

public class Loan {
    private final UUID id;
    private final UUID bookId;
    private final UUID patronId;
    private final LocalDate dueDate;
    private boolean isApproved;

    public Loan(UUID bookId, UUID patronId, LocalDate dueDate) {
        this.id = UUID.randomUUID();
        this.bookId = bookId;
        this.patronId = patronId;
        this.dueDate = dueDate;
        this.isApproved = false;
    }

    // GOOD - intention-revealing methods (Best Practices)
    public void approve() {
        this.isApproved = true;
    }
    
    public void reject() {
        this.isApproved = false;
    }

    public UUID getId() { return id; }
    public UUID getBookId() { return bookId; }
    public UUID getPatronId() { return patronId; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isApproved() { return isApproved; }
}
