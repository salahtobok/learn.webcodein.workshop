package com.webcodein.valhalla;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointTest {

    @Test
    @DisplayName("Should create Point with coordinates and verify accessors")
    void shouldCreatePoint() {
        Point point = new Point(42, 100);
        assertThat(point.getX()).isEqualTo(42);
        assertThat(point.getY()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should create StandardPoint and verify coordinates")
    void shouldCreateStandardPoint() {
        StandardPoint point = new StandardPoint(-15, 88);
        assertThat(point.getX()).isEqualTo(-15);
        assertThat(point.getY()).isEqualTo(88);
    }
}
