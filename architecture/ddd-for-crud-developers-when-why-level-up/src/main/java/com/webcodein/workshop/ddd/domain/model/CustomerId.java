package com.webcodein.workshop.ddd.domain.model;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId value cannot be null");
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }

    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }

    public static CustomerId fromString(String id) {
        Objects.requireNonNull(id, "CustomerId string cannot be null");
        return new CustomerId(UUID.fromString(id));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
