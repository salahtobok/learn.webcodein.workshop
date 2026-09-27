package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain;

public record ProductName(String value) {
    public ProductName {
        if (value == null || value.isBlank() || value.length() > 200) {
            throw new IllegalArgumentException("Product name must be 1-200 characters");
        }
    }
}
