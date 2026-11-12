package com.webcodein.nplusone.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

    @Test
    void shouldCreateBookWithPublisherAndAuthor() {
        Author author = new Author("Eric Evans", "evans@dddcommunity.org");
        Publisher publisher = new Publisher("Addison-Wesley");
        Book book = new Book("Domain-Driven Design", "978-0321125217", 55.00);

        author.addBook(book);
        publisher.addBook(book);

        assertThat(book.getTitle()).isEqualTo("Domain-Driven Design");
        assertThat(book.getIsbn()).isEqualTo("978-0321125217");
        assertThat(book.getPrice()).isEqualTo(55.00);
        assertThat(book.getAuthor()).isEqualTo(author);
        assertThat(book.getPublisher()).isEqualTo(publisher);
    }
}
