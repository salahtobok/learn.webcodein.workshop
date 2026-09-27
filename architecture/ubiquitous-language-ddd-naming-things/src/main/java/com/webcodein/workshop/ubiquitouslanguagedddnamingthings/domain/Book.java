package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import java.util.UUID;

public class Book {
    private final UUID id;
    private Isbn isbn;
    private String title;
    private boolean isLost;

    public Book(Isbn isbn, String title) {
        this.id = UUID.randomUUID();
        this.isbn = isbn;
        this.title = title;
        this.isLost = false;
    }

    // GOOD - Captures exactly WHY the book is being updated (Example 5)
    public void markBookAsLost() {
        this.isLost = true;
    }

    // GOOD - Captures exactly WHY the book is being updated (Example 5)
    public void correctBookTitle(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = newTitle;
    }

    public UUID getId() { return id; }
    public Isbn getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public boolean isLost() { return isLost; }
}
