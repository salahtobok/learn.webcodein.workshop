package com.webcodein.workshop.realtime.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessagePayloadTest {

    @Test
    @DisplayName("Should create valid text payload")
    void shouldCreateValidTextPayload() {
        MessagePayload payload = MessagePayload.text("s-1", "hello realtime");
        assertThat(payload.streamId()).isEqualTo("s-1");
        assertThat(new String(payload.data())).isEqualTo("hello realtime");
        assertThat(payload.isBinary()).isFalse();
    }

    @Test
    @DisplayName("Should create valid binary payload")
    void shouldCreateValidBinaryPayload() {
        byte[] bytes = new byte[]{0x01, 0x02, 0x03};
        MessagePayload payload = MessagePayload.binary("s-2", bytes);
        assertThat(payload.streamId()).isEqualTo("s-2");
        assertThat(payload.data()).containsExactly(0x01, 0x02, 0x03);
        assertThat(payload.isBinary()).isTrue();
    }

    @Test
    @DisplayName("Should reject null fields")
    void shouldRejectNullFields() {
        assertThatThrownBy(() -> new MessagePayload(null, new byte[0], false, 0))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new MessagePayload("s-1", null, false, 0))
                .isInstanceOf(NullPointerException.class);
    }
}
