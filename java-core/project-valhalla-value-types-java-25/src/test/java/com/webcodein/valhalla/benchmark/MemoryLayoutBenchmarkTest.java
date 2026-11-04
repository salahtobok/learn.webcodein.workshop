package com.webcodein.valhalla.benchmark;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryLayoutBenchmarkTest {

    @Test
    @DisplayName("Should initialize benchmark arrays and calculate identical deterministic sums")
    void shouldCalculateIdenticalSums() {
        MemoryLayoutBenchmark benchmark = new MemoryLayoutBenchmark();
        benchmark.setup();

        long valueSum = benchmark.benchmarkValueTypes();
        long objectSum = benchmark.benchmarkStandardObjects();

        assertThat(valueSum).isGreaterThan(0L);
        assertThat(valueSum).isEqualTo(objectSum);
    }
}
