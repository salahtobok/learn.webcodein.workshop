package com.bookstore.domain;

import java.util.UUID;

// Internal Entity, inaccessible directly from the outside (package-private ideally, or protected)
class Chapter {
    private UUID id;
    private String title;
    
    Chapter(UUID id, String title) {
        this.id = id;
        this.title = title;
    }
    
    public String getTitle() { return title; }
}
