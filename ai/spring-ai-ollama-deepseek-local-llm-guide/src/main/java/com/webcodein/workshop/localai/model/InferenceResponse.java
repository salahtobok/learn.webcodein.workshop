package com.webcodein.workshop.localai.model;

import java.util.Objects;

public record InferenceResponse(
        String content,
        String model,
        long durationMs,
        boolean success
) {
    public InferenceResponse {
        Objects.requireNonNull(content, "Content must not be null");
        Objects.requireNonNull(model, "Model must not be null");
    }

    public static InferenceResponse successful(String content, String model, long durationMs) {
        return new InferenceResponse(content, model, durationMs, true);
    }

    public static InferenceResponse failed(String errorMessage, String model) {
        return new InferenceResponse("Error: " + errorMessage, model, 0L, false);
    }
}
