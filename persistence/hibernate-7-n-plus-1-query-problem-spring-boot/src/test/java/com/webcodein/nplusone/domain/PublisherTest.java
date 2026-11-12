package com.webcodein.nplusone.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PublisherTest {

    @Test
    void shouldCreatePublisherAndManageBooks() {
        Publisher publisher = new Publisher("O'Reilly Media");
        Book book = new Book("Designing Data-Intensive Applications", "978-1449373320", 49.99);

        publisher.addBook(book);

        assertThat(publisher.getName()).isEqualTo("O'Reilly Media");
        assertThat(publisher.getBooks()).containsExactly(book);
        assertThat(book.getPublisher()).isEqualTo(publisher);
    }
}
