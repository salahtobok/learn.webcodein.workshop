package com.bookstore.infrastructure;

import com.bookstore.domain.BookAggregate;
import com.bookstore.domain.BookRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookRepository implements BookRepository {

    private final Map<UUID, BookAggregate> store = new ConcurrentHashMap<>();

    @Override
    public Optional<BookAggregate> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(BookAggregate book) {
        store.put(book.getId(), book);
    }

    @Override
    public void delete(BookAggregate book) {
        store.remove(book.getId());
    }
}
