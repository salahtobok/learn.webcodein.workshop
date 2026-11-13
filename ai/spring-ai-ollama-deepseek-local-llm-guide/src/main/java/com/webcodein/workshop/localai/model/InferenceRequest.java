package com.webcodein.workshop.localai.model;

import java.util.Objects;

public record InferenceRequest(
        String prompt,
        String model,
        double temperature
) {
    public InferenceRequest {
        Objects.requireNonNull(prompt, "Prompt cannot be null");
        if (prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be empty");
        }
        if (model == null || model.isBlank()) {
            model = "deepseek-r1:8b";
        }
        if (temperature < 0.0 || temperature > 2.0) {
            throw new IllegalArgumentException("Temperature must be between 0.0 and 2.0");
        }
    }

    public static InferenceRequest of(String prompt) {
        return new InferenceRequest(prompt, "deepseek-r1:8b", 0.7);
    }
}
