package com.bookstore.domain;

public class IsBestsellerSpec implements Specification<BookAggregate> {
    public boolean isSatisfiedBy(BookAggregate book) {
        return book.getSalesCount() > 10000;
    }
}
