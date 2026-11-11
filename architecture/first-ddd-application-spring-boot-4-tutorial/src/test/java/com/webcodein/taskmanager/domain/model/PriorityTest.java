package com.webcodein.taskmanager.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Priority Value Object Tests")
class PriorityTest {

    @Test
    @DisplayName("Should create valid Priority instances")
    void shouldCreateValidPriority() {
        Priority priority = new Priority(3, "HIGH");
        assertEquals(3, priority.level());
        assertEquals("HIGH", priority.name());
    }

    @Test
    @DisplayName("Should reject level below 1 or above 4")
    void shouldRejectInvalidLevel() {
        assertThrows(IllegalArgumentException.class, () -> new Priority(0, "INVALID"));
        assertThrows(IllegalArgumentException.class, () -> new Priority(5, "INVALID"));
    }

    @Test
    @DisplayName("Should resolve presets via of() factory")
    void shouldResolvePresets() {
        assertEquals(Priority.LOW, Priority.of(1));
        assertEquals(Priority.MEDIUM, Priority.of(2));
        assertEquals(Priority.HIGH, Priority.of(3));
        assertEquals(Priority.URGENT, Priority.of(4));
    }

    @Test
    @DisplayName("Should verify value object equality")
    void shouldVerifyValueEquality() {
        Priority p1 = new Priority(2, "MEDIUM");
        Priority p2 = new Priority(2, "MEDIUM");
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
