package com.webcodein.workshop.architecture.ddd.domain;

import com.webcodein.workshop.architecture.ddd.domain.model.order.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DDD Value Object: Quantity Tests")
class QuantityTest {

    @Test
    @DisplayName("Should accept valid quantity within range 1 to 100")
    void shouldAcceptValidQuantity() {
        Quantity q = Quantity.of(5);
        assertThat(q.value()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should reject quantity less than 1")
    void shouldRejectQuantityLessThanOne() {
        assertThatThrownBy(() -> Quantity.of(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 1");

        assertThatThrownBy(() -> Quantity.of(-3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should reject quantity exceeding 100 per line")
    void shouldRejectQuantityAboveHundred() {
        assertThatThrownBy(() -> Quantity.of(101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximum limit of 100");
    }

    @Test
    @DisplayName("Should add quantities together")
    void shouldAddQuantities() {
        Quantity q1 = Quantity.of(4);
        Quantity q2 = Quantity.of(6);

        Quantity sum = q1.add(q2);
        assertThat(sum.value()).isEqualTo(10);
    }
}
