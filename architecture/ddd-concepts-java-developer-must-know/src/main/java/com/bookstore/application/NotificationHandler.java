package com.bookstore.application;

import com.bookstore.domain.BookPublishedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// Spring Boot Event Listener
@Component
public class NotificationHandler {

    @EventListener
    public void onBookPublished(BookPublishedEvent event) {
        System.out.println("Notify users about new book: " + event.title());
        // Send emails, update search index, etc.
    }
}
