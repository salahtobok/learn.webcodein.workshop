package com.webcodein.webhook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "webhook_events")
public class WebhookEvent {

    @Id
    @Column(name = "event_id", updatable = false, nullable = false)
    private String eventId;

    @Column(name = "payload", columnDefinition = "text", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status = EventStatus.RECEIVED;

    public WebhookEvent() {}

    public WebhookEvent(String eventId, String payload) {
        this.eventId = eventId;
        this.payload = payload;
    }

    public String getEventId() {
        return eventId;
    }

    public String getPayload() {
        return payload;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
