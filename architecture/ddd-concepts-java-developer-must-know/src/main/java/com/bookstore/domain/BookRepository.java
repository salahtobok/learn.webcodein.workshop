package com.bookstore.domain;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository {
    // We deal with the entire Aggregate Root
    Optional<BookAggregate> findById(UUID id);
    
    void save(BookAggregate book);
    
    void delete(BookAggregate book);
}
