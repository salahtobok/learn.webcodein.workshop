package com.webcodein.workshop.realtime.model;

import java.util.Objects;

public record MessagePayload(
        String streamId,
        byte[] data,
        boolean isBinary,
        long timestamp
) {
    public MessagePayload {
        Objects.requireNonNull(streamId, "streamId must not be null");
        Objects.requireNonNull(data, "data must not be null");
    }

    public static MessagePayload text(String streamId, String text) {
        Objects.requireNonNull(text, "text must not be null");
        return new MessagePayload(streamId, text.getBytes(java.nio.charset.StandardCharsets.UTF_8), false, System.currentTimeMillis());
    }

    public static MessagePayload binary(String streamId, byte[] binaryData) {
        return new MessagePayload(streamId, binaryData, true, System.currentTimeMillis());
    }
}
