package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

// GOOD - Represents a real business concept (Example 2)
public class LibraryCatalog {
    
    public void registerNewBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Cannot register a null book.");
        }
        // Implementation here
    }
}
