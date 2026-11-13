package com.webcodein.workshop.quic.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StreamBufferTest {

    @Test
    @DisplayName("Should assemble out-of-order stream frames once missing gap is filled")
    void shouldAssembleOutOfOrderFrames() {
        StreamBuffer buffer = new StreamBuffer(10L);

        byte[] chunk1 = "AAAA".getBytes(StandardCharsets.UTF_8);
        byte[] chunk2 = "BBBB".getBytes(StandardCharsets.UTF_8);
        byte[] chunk3 = "CCCC".getBytes(StandardCharsets.UTF_8);

        StreamFrame frame2 = new StreamFrame(10L, 4L, chunk2, false);
        StreamFrame frame3 = new StreamFrame(10L, 8L, chunk3, true);
        StreamFrame frame1 = new StreamFrame(10L, 0L, chunk1, false);

        // Frame 2 and 3 arrive first (out of order gap at 0)
        buffer.pushFrame(frame2);
        buffer.pushFrame(frame3);

        byte[] deliveredBeforeGap = buffer.drainContiguousBytes();
        assertThat(deliveredBeforeGap).isEmpty();
        assertThat(buffer.nextExpectedOffset()).isEqualTo(0L);

        // Frame 1 arrives to fill the gap
        buffer.pushFrame(frame1);
        byte[] deliveredAll = buffer.drainContiguousBytes();

        assertThat(new String(deliveredAll, StandardCharsets.UTF_8)).isEqualTo("AAAABBBBCCCC");
        assertThat(buffer.nextExpectedOffset()).isEqualTo(12L);
        assertThat(buffer.isStreamFinished()).isTrue();
    }

    @Test
    @DisplayName("Should reject frame for different stream ID")
    void shouldRejectStreamMismatch() {
        StreamBuffer buffer = new StreamBuffer(1L);
        StreamFrame frame = new StreamFrame(2L, 0L, new byte[]{1}, true);

        assertThatThrownBy(() -> buffer.pushFrame(frame))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
