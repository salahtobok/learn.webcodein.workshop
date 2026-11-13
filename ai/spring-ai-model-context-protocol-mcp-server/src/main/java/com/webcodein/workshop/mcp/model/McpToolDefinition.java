package com.webcodein.workshop.mcp.model;

import java.util.Map;
import java.util.Objects;

public record McpToolDefinition(
        String name,
        String description,
        Map<String, Object> inputSchema,
        boolean readOnly
) {
    public McpToolDefinition {
        Objects.requireNonNull(name, "Tool name must not be null");
        Objects.requireNonNull(description, "Description must not be null");
        Objects.requireNonNull(inputSchema, "Input schema must not be null");
    }
}
