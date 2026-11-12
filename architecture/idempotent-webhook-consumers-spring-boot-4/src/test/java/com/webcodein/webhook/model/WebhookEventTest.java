package com.webcodein.webhook.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebhookEventTest {

    @Test
    @DisplayName("Should initialize WebhookEvent with RECEIVED status")
    void testConstructorAndDefaultStatus() {
        WebhookEvent event = new WebhookEvent("evt_abc", "{\"type\":\"charge.success\"}");

        assertEquals("evt_abc", event.getEventId());
        assertEquals("{\"type\":\"charge.success\"}", event.getPayload());
        assertEquals(EventStatus.RECEIVED, event.getStatus());
    }

    @Test
    @DisplayName("Should update status to PROCESSED")
    void testSetStatusProcessed() {
        WebhookEvent event = new WebhookEvent("evt_abc", "{}");
        event.setStatus(EventStatus.PROCESSED);

        assertEquals(EventStatus.PROCESSED, event.getStatus());
    }

    @Test
    @DisplayName("Should update status to FAILED")
    void testSetStatusFailed() {
        WebhookEvent event = new WebhookEvent("evt_abc", "{}");
        event.setStatus(EventStatus.FAILED);

        assertEquals(EventStatus.FAILED, event.getStatus());
    }
}
