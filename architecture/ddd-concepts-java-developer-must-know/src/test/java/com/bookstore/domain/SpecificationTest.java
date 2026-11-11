package com.bookstore.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SpecificationTest {

    private final Money standardPrice = new Money(new BigDecimal("50.00"), "USD");

    @Test
    @DisplayName("IsBestsellerSpec satisfies only when salesCount > 10000")
    void testIsBestsellerSpec() {
        IsBestsellerSpec spec = new IsBestsellerSpec();

        BookAggregate bestseller = new BookAggregate(UUID.randomUUID(), "Harry Potter", standardPrice, false, 15000, 10);
        BookAggregate regular = new BookAggregate(UUID.randomUUID(), "Niche Topic", standardPrice, false, 500, 10);

        assertThat(spec.isSatisfiedBy(bestseller)).isTrue();
        assertThat(spec.isSatisfiedBy(regular)).isFalse();
    }

    @Test
    @DisplayName("IsInStockSpec satisfies only when stock > 0")
    void testIsInStockSpec() {
        IsInStockSpec spec = new IsInStockSpec();

        BookAggregate inStock = new BookAggregate(UUID.randomUUID(), "Java in Action", standardPrice, false, 200, 5);
        BookAggregate outOfStock = new BookAggregate(UUID.randomUUID(), "Old Print", standardPrice, false, 200, 0);

        assertThat(spec.isSatisfiedBy(inStock)).isTrue();
        assertThat(spec.isSatisfiedBy(outOfStock)).isFalse();
    }

    @Test
    @DisplayName("Composed specification applies promotion only when bestseller AND in stock")
    void testComposedSpecificationPromotion() {
        PromotionService service = new PromotionService();

        // Eligible: bestseller (12000 sales) and in stock (5)
        BookAggregate eligible = new BookAggregate(UUID.randomUUID(), "Bestseller Hit", new Money(new BigDecimal("100.00"), "USD"), false, 12000, 5);
        service.applyPromotion(eligible);
        assertThat(eligible.getPrice().amount()).isEqualByComparingTo("90.00");

        // Ineligible: bestseller but out of stock
        BookAggregate outOfStockBestseller = new BookAggregate(UUID.randomUUID(), "Sold Out Hit", new Money(new BigDecimal("100.00"), "USD"), false, 12000, 0);
        service.applyPromotion(outOfStockBestseller);
        assertThat(outOfStockBestseller.getPrice().amount()).isEqualByComparingTo("100.00");

        // Ineligible: in stock but not bestseller
        BookAggregate regularBook = new BookAggregate(UUID.randomUUID(), "Standard Book", new Money(new BigDecimal("100.00"), "USD"), false, 500, 10);
        service.applyPromotion(regularBook);
        assertThat(regularBook.getPrice().amount()).isEqualByComparingTo("100.00");
    }
}
