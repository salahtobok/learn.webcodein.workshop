package com.webcodein.nplusone.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorTest {

    @Test
    void shouldCreateAuthorWithBooks() {
        Author author = new Author("Martin Fowler", "fowler@refactoring.com");
        Book book = new Book("Refactoring", "978-0134757599", 49.99);

        author.addBook(book);

        assertThat(author.getName()).isEqualTo("Martin Fowler");
        assertThat(author.getEmail()).isEqualTo("fowler@refactoring.com");
        assertThat(author.getBooks()).containsExactly(book);
        assertThat(book.getAuthor()).isEqualTo(author);
    }

    @Test
    void shouldRemoveBookCleanly() {
        Author author = new Author("Joshua Bloch", "bloch@effectivejava.com");
        Book book = new Book("Effective Java", "978-0134685991", 45.00);

        author.addBook(book);
        author.removeBook(book);

        assertThat(author.getBooks()).isEmpty();
        assertThat(book.getAuthor()).isNull();
    }
}
