package com.webcodein.workshop.ai.hybrid.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public record DocumentRecord(
        @NotBlank(message = "Document ID cannot be blank")
        String id,

        @NotBlank(message = "Title cannot be blank")
        String title,

        @NotBlank(message = "Content cannot be blank")
        String content,

        Map<String, String> metadata,

        @NotNull(message = "Embedding vector cannot be null")
        List<Double> embedding
) {
    public DocumentRecord {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Document ID cannot be blank");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be blank");
        }
        if (embedding == null || embedding.isEmpty()) {
            throw new IllegalArgumentException("Embedding vector must contain elements");
        }
    }
}
