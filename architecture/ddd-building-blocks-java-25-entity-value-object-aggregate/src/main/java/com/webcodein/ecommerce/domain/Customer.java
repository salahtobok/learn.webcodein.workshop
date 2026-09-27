package com.webcodein.ecommerce.domain;

import java.util.UUID;
import java.util.Objects;
import java.time.LocalDateTime;

public class Customer {
    private final UUID customerId; // The unique and immutable identity
    private String fullName;
    private String emailAddress;
    private LocalDateTime memberSince;
    private boolean isActive;

    // Constructor creates a new valid state
    public Customer(UUID customerId, String fullName, String emailAddress) {
        if (customerId == null) throw new IllegalArgumentException("Customer ID cannot be null");
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        
        this.customerId = customerId;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.memberSince = LocalDateTime.now();
        this.isActive = true;
    }

    public UUID getCustomerId() { return customerId; }
    public String getFullName() { return fullName; }
    public String getEmailAddress() { return emailAddress; }
    public boolean isActive() { return isActive; }
    
    // Business logic methods, not just setters
    public void changeEmailAddress(String newEmail) {
        if (newEmail == null || !newEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.emailAddress = newEmail;
    }

    public void deactivateAccount() {
        this.isActive = false;
    }

    // Equality is based ONLY on the identity (customerId)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return customerId.equals(customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}
