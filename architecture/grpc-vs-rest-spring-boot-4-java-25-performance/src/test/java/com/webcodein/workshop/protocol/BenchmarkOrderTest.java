package com.webcodein.workshop.protocol;

import com.webcodein.workshop.protocol.model.BenchmarkOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BenchmarkOrderTest {

    @Test
    @DisplayName("Sample order creates valid properties")
    void testSampleOrder() {
        BenchmarkOrder sample = BenchmarkOrder.sample();

        assertThat(sample.orderId()).isEqualTo("ORD-BENCH-99");
        assertThat(sample.amount()).isEqualTo(1249.50);
        assertThat(sample.itemCount()).isEqualTo(4);
    }

    @Test
    @DisplayName("Invalid amount throws IllegalArgumentException")
    void testInvalidAmount() {
        assertThatThrownBy(() -> new BenchmarkOrder("ORD-1", "CUST-1", -10.0, 1, 1000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be positive");
    }
}
