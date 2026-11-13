package com.webcodein.workshop.realtime.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FrameResultTest {

    @Test
    @DisplayName("Should calculate correct overhead ratio")
    void shouldCalculateCorrectOverheadRatio() {
        FrameResult result = new FrameResult("TestProtocol", 10, 90, 100, DeliveryGuarantee.RELIABLE_ORDERED, 500);
        assertThat(result.overheadRatio()).isEqualTo(0.10);
        assertThat(result.protocolName()).isEqualTo("TestProtocol");
    }

    @Test
    @DisplayName("Should reject negative byte counts")
    void shouldRejectNegativeByteCounts() {
        assertThatThrownBy(() -> new FrameResult("Test", -1, 10, 9, DeliveryGuarantee.RELIABLE_ORDERED, 100))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
