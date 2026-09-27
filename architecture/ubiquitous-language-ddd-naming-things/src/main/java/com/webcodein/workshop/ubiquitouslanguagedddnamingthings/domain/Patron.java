package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import java.util.UUID;

// GOOD - Extremely specific to the library domain (Example 1, 7)
public class Patron {
    private final UUID id;
    private PatronProfile profile;
    private boolean isActive;

    public Patron(PatronProfile profile) {
        this.id = UUID.randomUUID();
        this.profile = profile;
        this.isActive = true;
    }

    // Ubiquitous Language: The code must read like a sentence spoken by the business expert.
    public void borrow(Book book) {
        if (!isActive) {
            throw new IllegalStateException("Inactive patrons cannot borrow books.");
        }
        // Implementation here
    }

    // GOOD - The intention is instantly clear to any reader (Example 4)
    public void activateAccount() {
        this.isActive = true;
    }

    // GOOD - The intention is instantly clear to any reader (Example 4)
    public void suspendAccount() {
        this.isActive = false;
    }

    // GOOD - Uses a Value Object that encapsulates rules (Example 8)
    public void payFine(Money amount) {
        // Implementation here
    }

    public UUID getId() { return id; }
    public PatronProfile getProfile() { return profile; }
    public boolean isActive() { return isActive; }
}
