package com.bookstore.domain;

import java.time.Instant;
import java.util.UUID;

// The Event (Immutable)
public record BookPublishedEvent(
    UUID bookId, 
    String title, 
    Instant timestamp
) {}
