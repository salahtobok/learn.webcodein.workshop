package com.bookstore.sales.domain;

import java.math.BigDecimal;

public record Book(
    String isbn, 
    BigDecimal price, 
    boolean inStock
) {
    public boolean canBeSold() {
        return inStock && price.compareTo(BigDecimal.ZERO) > 0;
    }
}
