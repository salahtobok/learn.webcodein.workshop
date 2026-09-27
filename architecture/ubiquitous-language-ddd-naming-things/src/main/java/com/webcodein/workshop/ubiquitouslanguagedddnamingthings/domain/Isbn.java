package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import java.util.Objects;

// GOOD - Uses a Value Object that encapsulates rules (Example 8 & Best Practices)
public record Isbn(String value) {
    public Isbn {
        Objects.requireNonNull(value, "ISBN cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }
        // Basic validation logic would go here
    }
}
