package com.webcodein.workshop.fullstack.unit;

import com.webcodein.workshop.fullstack.model.PriceUpdateEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceUpdateEventTest {

    @Test
    @DisplayName("Should create valid PriceUpdateEvent")
    void shouldCreateValidPriceUpdateEvent() {
        PriceUpdateEvent event = new PriceUpdateEvent(
                "AAPL",
                new BigDecimal("235.50"),
                new BigDecimal("1.45"),
                101L,
                null
        );

        assertThat(event.ticker()).isEqualTo("AAPL");
        assertThat(event.price()).isEqualByComparingTo(new BigDecimal("235.50"));
        assertThat(event.changePercent()).isEqualByComparingTo(new BigDecimal("1.45"));
        assertThat(event.sequenceNumber()).isEqualTo(101L);
        assertThat(event.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should fail when price is non-positive")
    void shouldFailWhenPriceIsNonPositive() {
        assertThatThrownBy(() -> new PriceUpdateEvent("AAPL", BigDecimal.ZERO, BigDecimal.ZERO, 1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price must be positive");
    }

    @Test
    @DisplayName("Should fail when ticker is blank")
    void shouldFailWhenTickerIsBlank() {
        assertThatThrownBy(() -> new PriceUpdateEvent("   ", BigDecimal.TEN, BigDecimal.ZERO, 1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ticker must not be blank");
    }
}
