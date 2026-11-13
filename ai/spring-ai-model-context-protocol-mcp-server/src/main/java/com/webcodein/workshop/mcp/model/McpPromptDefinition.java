package com.webcodein.workshop.mcp.model;

import java.util.List;
import java.util.Objects;

public record McpPromptDefinition(
        String name,
        String description,
        List<String> requiredArguments,
        String template
) {
    public McpPromptDefinition {
        Objects.requireNonNull(name, "Prompt name must not be null");
        Objects.requireNonNull(description, "Description must not be null");
        Objects.requireNonNull(requiredArguments, "Arguments list must not be null");
        Objects.requireNonNull(template, "Template string must not be null");
    }
}
