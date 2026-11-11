package com.webcodein.workshop.ddd.domain;

public record Email(String value) {
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        String trimmed = value.trim();
        if (!trimmed.contains("@") || !trimmed.contains(".")) {
            throw new IllegalArgumentException("Email format is invalid: " + value);
        }
        value = trimmed.toLowerCase();
    }
}
