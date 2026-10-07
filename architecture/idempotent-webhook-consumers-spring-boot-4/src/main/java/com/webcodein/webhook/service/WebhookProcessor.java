package com.webcodein.webhook.service;

import com.webcodein.webhook.model.EventStatus;
import com.webcodein.webhook.model.WebhookEvent;
import com.webcodein.webhook.repository.WebhookEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WebhookProcessor {

    private final WebhookEventRepository repository;

    public WebhookProcessor(WebhookEventRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void processPendingEvents() {
        List<WebhookEvent> pending = repository.findByStatus(EventStatus.RECEIVED);
        
        for (WebhookEvent event : pending) {
            try {
                // Execute business logic (e.g., Update Order Status)
                processBusinessLogic(event.getPayload());
                
                event.setStatus(EventStatus.PROCESSED);
                repository.save(event);
            } catch (Exception e) {
                event.setStatus(EventStatus.FAILED);
                repository.save(event);
            }
        }
    }
    
    private void processBusinessLogic(String payload) {
        System.out.println("Processing payload: " + payload);
    }
}
