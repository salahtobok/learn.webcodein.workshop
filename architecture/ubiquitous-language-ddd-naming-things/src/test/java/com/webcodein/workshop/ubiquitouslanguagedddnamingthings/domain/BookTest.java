package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    @DisplayName("Should create book with initial unlost state and assigned UUID")
    void createBook() {
        Isbn isbn = new Isbn("978-0134685991");
        Book book = new Book(isbn, "Effective Java");

        assertNotNull(book.getId());
        assertEquals(isbn, book.getIsbn());
        assertEquals("Effective Java", book.getTitle());
        assertFalse(book.isLost());
    }

    @Test
    @DisplayName("Should mark book as lost using explicit ubiquitous domain method")
    void markBookAsLost() {
        Book book = new Book(new Isbn("978-0134685991"), "Effective Java");
        book.markBookAsLost();
        assertTrue(book.isLost());
    }

    @Test
    @DisplayName("Should correct book title with valid new title")
    void correctBookTitle() {
        Book book = new Book(new Isbn("978-0134685991"), "Old Title");
        book.correctBookTitle("Clean Code");
        assertEquals("Clean Code", book.getTitle());
    }

    @Test
    @DisplayName("Should reject blank new title when correcting book title")
    void rejectBlankTitle() {
        Book book = new Book(new Isbn("978-0134685991"), "Old Title");
        assertThrows(IllegalArgumentException.class, () -> book.correctBookTitle(""));
        assertThrows(IllegalArgumentException.class, () -> book.correctBookTitle("   "));
        assertThrows(IllegalArgumentException.class, () -> book.correctBookTitle(null));
    }
}
