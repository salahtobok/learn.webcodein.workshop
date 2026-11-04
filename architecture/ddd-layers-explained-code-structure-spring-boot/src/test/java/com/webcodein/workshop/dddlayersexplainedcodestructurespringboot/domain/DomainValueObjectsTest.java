package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainValueObjectsTest {

    @Test
    @DisplayName("Should validate Money invariants")
    void shouldValidateMoney() {
        Money money = new Money(new BigDecimal("99.50"), "EUR");
        assertThat(money.amount()).isEqualTo(new BigDecimal("99.50"));
        assertThat(money.currency()).isEqualTo("EUR");

        assertThatThrownBy(() -> new Money(new BigDecimal("-1.00"), "USD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Amount cannot be negative");
    }

    @Test
    @DisplayName("Should validate ProductName invariants")
    void shouldValidateProductName() {
        ProductName name = new ProductName("Valid Product");
        assertThat(name.value()).isEqualTo("Valid Product");

        assertThatThrownBy(() -> new ProductName(""))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ProductName(null))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ProductName("A".repeat(201)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should validate ProductId invariants and factory generation")
    void shouldValidateProductId() {
        UUID uuid = UUID.randomUUID();
        ProductId id = new ProductId(uuid);
        assertThat(id.value()).isEqualTo(uuid);

        ProductId generated = ProductId.generate();
        assertThat(generated.value()).isNotNull();

        assertThatThrownBy(() -> new ProductId(null))
            .isInstanceOf(NullPointerException.class);
    }
}
