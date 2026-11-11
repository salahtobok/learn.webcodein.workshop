package com.bookstore.application;

import com.bookstore.domain.BookAggregate;
import com.bookstore.domain.BookRepository;
import com.bookstore.domain.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookApplicationServiceTest {

    static class InMemoryBookRepository implements BookRepository {
        private final Map<UUID, BookAggregate> storage = new HashMap<>();

        @Override
        public Optional<BookAggregate> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public void save(BookAggregate book) {
            storage.put(book.getId(), book);
        }

        @Override
        public void delete(BookAggregate book) {
            storage.remove(book.getId());
        }
    }

    @Test
    @DisplayName("Application service should load aggregate, apply domain logic, and persist changes")
    void shouldAddChapterThroughApplicationService() {
        InMemoryBookRepository repository = new InMemoryBookRepository();
        BookApplicationService service = new BookApplicationService(repository);

        UUID bookId = UUID.randomUUID();
        BookAggregate book = new BookAggregate(bookId, "Effective Java", new Money(new BigDecimal("45.00"), "USD"));
        repository.save(book);

        service.addChapterToBook(bookId, "Item 1: Static Factories");

        BookAggregate updated = repository.findById(bookId).orElseThrow();
        assertThat(updated.getChapterTitles()).containsExactly("Item 1: Static Factories");
    }

    @Test
    @DisplayName("Application service should fail when book id does not exist")
    void shouldThrowWhenBookNotFound() {
        InMemoryBookRepository repository = new InMemoryBookRepository();
        BookApplicationService service = new BookApplicationService(repository);

        UUID nonExistentId = UUID.randomUUID();
        assertThatThrownBy(() -> service.addChapterToBook(nonExistentId, "Chapter 1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Book not found");
    }
}
