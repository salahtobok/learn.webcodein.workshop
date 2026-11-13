package com.webcodein.workshop.quic.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;

class StreamFrameTest {

    @Test
    @DisplayName("Should create valid stream frame and enforce payload defensive copy")
    void shouldCreateValidStreamFrame() {
        byte[] original = "hello quic".getBytes(StandardCharsets.UTF_8);
        StreamFrame frame = new StreamFrame(1L, 0L, original, true);

        assertThat(frame.streamId()).isEqualTo(1L);
        assertThat(frame.offset()).isEqualTo(0L);
        assertThat(frame.fin()).isTrue();
        assertThat(frame.length()).isEqualTo(original.length);

        // Modifying original array must not mutate frame payload
        original[0] = 'X';
        assertThat(new String(frame.payload(), StandardCharsets.UTF_8)).isEqualTo("hello quic");
    }

    @Test
    @DisplayName("Should reject invalid stream ID or negative offset")
    void shouldRejectInvalidArguments() {
        byte[] payload = "test".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> new StreamFrame(-1L, 0L, payload, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Stream ID");

        assertThatThrownBy(() -> new StreamFrame(1L, -5L, payload, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Offset");

        assertThatThrownBy(() -> new StreamFrame(1L, 0L, null, false))
            .isInstanceOf(NullPointerException.class);
    }
}
