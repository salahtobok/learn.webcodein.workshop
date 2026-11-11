package com.bookstore;

import com.bookstore.shipping.domain.Dimensions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BoundedContextTest {

    @Test
    @DisplayName("Sales context Book models commerce invariants independently")
    void shouldModelSalesBookInvariants() {
        com.bookstore.sales.domain.Book sellableBook =
                new com.bookstore.sales.domain.Book("978-0134494166", new BigDecimal("49.99"), true);
        com.bookstore.sales.domain.Book outOfStockBook =
                new com.bookstore.sales.domain.Book("978-0134494166", new BigDecimal("49.99"), false);
        com.bookstore.sales.domain.Book freeBook =
                new com.bookstore.sales.domain.Book("978-0134494166", BigDecimal.ZERO, true);

        assertThat(sellableBook.canBeSold()).isTrue();
        assertThat(outOfStockBook.canBeSold()).isFalse();
        assertThat(freeBook.canBeSold()).isFalse();
    }

    @Test
    @DisplayName("Shipping context Book models logistics and packaging independently")
    void shouldModelShippingBookInvariants() {
        Dimensions normalDimensions = new Dimensions(20.0, 15.0, 3.0);
        Dimensions oversizeDimensions = new Dimensions(60.0, 40.0, 10.0);

        com.bookstore.shipping.domain.Book lightBook =
                new com.bookstore.shipping.domain.Book("978-0134494166", 1.5, normalDimensions);
        com.bookstore.shipping.domain.Book heavyBook =
                new com.bookstore.shipping.domain.Book("978-0134494166", 6.2, normalDimensions);
        com.bookstore.shipping.domain.Book oversizedBook =
                new com.bookstore.shipping.domain.Book("978-0134494166", 2.0, oversizeDimensions);

        assertThat(lightBook.requiresOversizePackaging()).isFalse();
        assertThat(heavyBook.requiresOversizePackaging()).isTrue();
        assertThat(oversizedBook.requiresOversizePackaging()).isTrue();
    }
}
