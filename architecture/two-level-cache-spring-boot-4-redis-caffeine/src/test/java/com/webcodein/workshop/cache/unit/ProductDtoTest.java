package com.webcodein.workshop.cache.unit;

import com.webcodein.workshop.cache.model.ProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductDtoTest {

    @Test
    @DisplayName("Should create valid ProductDto record")
    void shouldCreateValidProductDto() {
        ProductDto dto = new ProductDto("P-1", "Enterprise Gateway", new BigDecimal("199.99"), 25);

        assertThat(dto.id()).isEqualTo("P-1");
        assertThat(dto.name()).isEqualTo("Enterprise Gateway");
        assertThat(dto.price()).isEqualByComparingTo("199.99");
        assertThat(dto.stockQuantity()).isEqualTo(25);
    }

    @Test
    @DisplayName("Should reject blank product ID")
    void shouldRejectBlankProductId() {
        assertThatThrownBy(() -> new ProductDto("", "Test", new BigDecimal("10.00"), 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product ID cannot be blank");
    }

    @Test
    @DisplayName("Should reject negative or zero price")
    void shouldRejectNegativePrice() {
        assertThatThrownBy(() -> new ProductDto("P-1", "Test", new BigDecimal("0.00"), 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price must be strictly positive");
    }
}
