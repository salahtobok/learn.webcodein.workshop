package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IsbnTest {

    @Test
    @DisplayName("Should create valid ISBN value object")
    void createValidIsbn() {
        Isbn isbn = new Isbn("978-0321125217");
        assertEquals("978-0321125217", isbn.value());
    }

    @Test
    @DisplayName("Should throw NullPointerException when ISBN is null")
    void rejectNullIsbn() {
        assertThrows(NullPointerException.class, () -> new Isbn(null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when ISBN is blank")
    void rejectBlankIsbn() {
        assertThrows(IllegalArgumentException.class, () -> new Isbn(""));
        assertThrows(IllegalArgumentException.class, () -> new Isbn("   "));
    }

    @Test
    @DisplayName("Should support value equality for identical ISBN codes")
    void isbnEquality() {
        Isbn isbn1 = new Isbn("978-0321125217");
        Isbn isbn2 = new Isbn("978-0321125217");
        assertEquals(isbn1, isbn2);
        assertEquals(isbn1.hashCode(), isbn2.hashCode());
    }
}
