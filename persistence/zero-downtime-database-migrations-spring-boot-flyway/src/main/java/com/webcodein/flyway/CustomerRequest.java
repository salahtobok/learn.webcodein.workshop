package com.webcodein.flyway;

public record CustomerRequest(String email, String fullName) {
    public CustomerRequest {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name must not be empty");
        }
    }
}
