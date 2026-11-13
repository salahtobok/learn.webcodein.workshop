package com.webcodein.workshop.mcp.model;

import java.util.Objects;

public record McpResourceDefinition(
        String uri,
        String name,
        String mimeType,
        String description
) {
    public McpResourceDefinition {
        Objects.requireNonNull(uri, "Resource URI must not be null");
        Objects.requireNonNull(name, "Resource name must not be null");
        Objects.requireNonNull(mimeType, "MIME type must not be null");
        Objects.requireNonNull(description, "Description must not be null");
    }
}
