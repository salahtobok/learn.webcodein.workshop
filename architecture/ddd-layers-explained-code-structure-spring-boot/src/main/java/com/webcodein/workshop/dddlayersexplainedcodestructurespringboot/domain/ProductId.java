package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain;

import java.util.UUID;
import java.util.Objects;

public record ProductId(UUID value) {
    public ProductId {
        Objects.requireNonNull(value, "ProductId cannot be null");
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }
}
