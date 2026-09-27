package com.bookstore.shipping.domain;

public record Dimensions(double length, double width, double height) {
    public boolean isOversized() {
        return length > 50 || width > 50 || height > 50; // simple mock logic
    }
}
