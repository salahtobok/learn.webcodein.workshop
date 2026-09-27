package com.bookstore.application;

import com.bookstore.domain.BookAggregate;
import com.bookstore.domain.BookRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookApplicationService {
    private final BookRepository repository;
    
    public BookApplicationService(BookRepository repository) {
        this.repository = repository;
    }
    
    public void addChapterToBook(UUID bookId, String title) {
        BookAggregate book = repository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));
            
        book.addChapter(title);
        
        repository.save(book); // Saves the root and its chapters
    }
}
