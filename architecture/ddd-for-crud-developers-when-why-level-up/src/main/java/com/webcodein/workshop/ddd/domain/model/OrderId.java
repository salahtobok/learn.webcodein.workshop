package com.webcodein.workshop.ddd.domain.model;

import java.util.Objects;
import java.util.UUID;

public record OrderId(UUID value) {
    public OrderId {
        Objects.requireNonNull(value, "OrderId value cannot be null");
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId of(UUID value) {
        return new OrderId(value);
    }

    public static OrderId fromString(String id) {
        Objects.requireNonNull(id, "OrderId string cannot be null");
        return new OrderId(UUID.fromString(id));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
