package com.webcodein.webhook.repository;

import com.webcodein.webhook.model.EventStatus;
import com.webcodein.webhook.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, String> {
    List<WebhookEvent> findByStatus(EventStatus status);
}
