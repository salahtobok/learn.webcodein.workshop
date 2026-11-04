package com.webcodein.boundedcontext.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    @DisplayName("Should create valid product in Catalog context")
    void shouldCreateValidProduct() {
        Product product = new Product("SKU-100", "Ergonomic Keyboard", "Mechanical keyboard", 149.99);

        assertThat(product.getId()).isNotNull();
        assertThat(product.getSku()).isEqualTo("SKU-100");
        assertThat(product.getName()).isEqualTo("Ergonomic Keyboard");
        assertThat(product.getDescription()).isEqualTo("Mechanical keyboard");
        assertThat(product.getPrice()).isEqualTo(149.99);
    }

    @Test
    @DisplayName("Should reject blank SKU in Catalog context")
    void shouldRejectBlankSku() {
        assertThatThrownBy(() -> new Product("   ", "Keyboard", "Desc", 99.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("SKU cannot be null or blank");
    }

    @Test
    @DisplayName("Should reject negative price in Catalog context")
    void shouldRejectNegativePrice() {
        assertThatThrownBy(() -> new Product("SKU-200", "Mouse", "Desc", -10.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("price cannot be negative");
    }

    @Test
    @DisplayName("Should update product price successfully")
    void shouldUpdatePrice() {
        Product product = new Product("SKU-300", "Monitor", "4K Display", 399.99);
        product.updatePrice(349.99);

        assertThat(product.getPrice()).isEqualTo(349.99);
    }
}
