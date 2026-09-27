package com.bookstore.domain;

public class IsInStockSpec implements Specification<BookAggregate> {
    public boolean isSatisfiedBy(BookAggregate book) {
        return book.getStock() > 0;
    }
}
