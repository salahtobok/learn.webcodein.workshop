package com.webcodein.documents.domain.model;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class DocumentTitle {
    private String value;

    protected DocumentTitle() {}

    public DocumentTitle(String value) {
        if (value == null || value.trim().length() < 3) {
            throw new IllegalArgumentException("Title must be at least 3 characters");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentTitle that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
