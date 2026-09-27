package com.bookstore.domain;

public interface Specification<T> {
    boolean isSatisfiedBy(T item);
    
    default Specification<T> and(Specification<T> other) {
        return item -> this.isSatisfiedBy(item) && other.isSatisfiedBy(item);
    }
}
