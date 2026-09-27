package com.webcodein.workshop.ddd.domain;

public record Email(String value) {
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!value.contains("@") || !value.contains(".")) {
            throw new IllegalArgumentException("Email format is invalid");
        }
    }
}
