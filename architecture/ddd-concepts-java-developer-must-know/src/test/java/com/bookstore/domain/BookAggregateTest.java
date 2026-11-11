package com.bookstore.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookAggregateTest {

    @Test
    @DisplayName("Aggregate root enforces invariant when adding chapters")
    void shouldAddChapterThroughRoot() {
        Money price = new Money(new BigDecimal("39.99"), "USD");
        BookAggregate book = new BookAggregate(UUID.randomUUID(), "Domain-Driven Design", price);

        book.addChapter("1. Strategic Design");
        book.addChapter("2. Tactical Patterns");

        assertThat(book.getChapterTitles()).containsExactly(
                "1. Strategic Design",
                "2. Tactical Patterns"
        );
    }

    @Test
    @DisplayName("Aggregate root rejects null or blank chapter titles")
    void shouldRejectInvalidChapterTitle() {
        Money price = new Money(new BigDecimal("39.99"), "USD");
        BookAggregate book = new BookAggregate(UUID.randomUUID(), "Domain-Driven Design", price);

        assertThatThrownBy(() -> book.addChapter(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chapter title is required");

        assertThatThrownBy(() -> book.addChapter("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chapter title is required");
    }

    @Test
    @DisplayName("Should apply 10% discount to price")
    void shouldApplyDiscount() {
        Money price = new Money(new BigDecimal("100.00"), "USD");
        BookAggregate book = new BookAggregate(UUID.randomUUID(), "Refactoring", price);

        book.applyDiscount();

        assertThat(book.getPrice().amount()).isEqualByComparingTo("90.00");
    }

    @Test
    @DisplayName("Should maintain inventory stock and sales metrics")
    void shouldMaintainStockAndSales() {
        Money price = new Money(new BigDecimal("50.00"), "USD");
        BookAggregate book = new BookAggregate(UUID.randomUUID(), "Clean Code", price, false, 500, 20);

        book.restock(30);
        assertThat(book.getStock()).isEqualTo(50);

        book.recordSales(10);
        assertThat(book.getSalesCount()).isEqualTo(510);
    }
}
