package com.webcodein.workshop.architecture.ddd.domain.model.order;

import java.util.Objects;

public record BookId(String value) {
    public BookId {
        Objects.requireNonNull(value, "BookId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("BookId cannot be empty or blank");
        }
    }
}
