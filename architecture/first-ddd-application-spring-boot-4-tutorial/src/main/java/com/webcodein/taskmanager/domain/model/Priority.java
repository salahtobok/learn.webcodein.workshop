package com.webcodein.taskmanager.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Priority(
        @Column(name = "priority_level") int level,
        @Column(name = "priority_name") String name
) {
    public static final Priority LOW = new Priority(1, "LOW");
    public static final Priority MEDIUM = new Priority(2, "MEDIUM");
    public static final Priority HIGH = new Priority(3, "HIGH");
    public static final Priority URGENT = new Priority(4, "URGENT");

    public Priority {
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Priority level must be between 1 and 4, got: " + level);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Priority name cannot be empty");
        }
    }

    public static Priority of(int level) {
        return switch (level) {
            case 1 -> LOW;
            case 2 -> MEDIUM;
            case 3 -> HIGH;
            case 4 -> URGENT;
            default -> throw new IllegalArgumentException("Invalid priority level: " + level);
        };
    }
}
