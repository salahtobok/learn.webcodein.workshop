package com.webcodein.workshop.architecture.ddd.domain.model.order;

public record Quantity(int value) {
    public Quantity {
        if (value < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1: " + value);
        }
        if (value > 100) {
            throw new IllegalArgumentException("Quantity cannot exceed maximum limit of 100 per line: " + value);
        }
    }

    public static Quantity of(int value) {
        return new Quantity(value);
    }

    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }
}
