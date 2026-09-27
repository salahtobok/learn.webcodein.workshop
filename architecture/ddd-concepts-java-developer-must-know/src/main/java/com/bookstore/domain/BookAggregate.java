package com.bookstore.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

// The Aggregate Root
public class BookAggregate {
    private UUID id;
    private String title;
    private Money price;
    private boolean clearance;
    private int salesCount;
    private int stock;
    
    // Internal Entity protected by the Root
    private List<Chapter> chapters = new ArrayList<>();

    public BookAggregate(UUID id, String title, Money price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }

    // Business logic enforced by the Root
    public void addChapter(String chapterTitle) {
        if (chapterTitle == null || chapterTitle.isBlank()) {
            throw new IllegalArgumentException("Chapter title is required");
        }
        this.chapters.add(new Chapter(UUID.randomUUID(), chapterTitle));
    }
    
    public List<String> getChapterTitles() {
        return chapters.stream().map(Chapter::getTitle).toList();
    }
    
    public Money getPrice() { return price; }
    public boolean isClearance() { return clearance; }
    public int getSalesCount() { return salesCount; }
    public int getStock() { return stock; }
    
    public void applyDiscount() {
        this.price = this.price.multiply(0.9); // Example discount
    }
}
