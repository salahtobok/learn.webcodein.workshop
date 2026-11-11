package com.webcodein.workshop.architecture.ddd.domain.model.order;

import java.util.Objects;
import java.util.regex.Pattern;

public record CustomerEmail(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public CustomerEmail {
        Objects.requireNonNull(value, "Email cannot be null");
        String trimmed = value.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid customer email format: " + value);
        }
        value = trimmed.toLowerCase();
    }
}
