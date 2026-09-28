package com.webcodein.kafka.service;

import com.webcodein.kafka.config.KafkaTopicConfig;
import com.webcodein.kafka.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderProducerService {

    private static final Logger log = LoggerFactory.getLogger(OrderProducerService.class);
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public OrderProducerService(KafkaTemplate<Object, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderEvent sendOrder(double amount) {
        String orderId = UUID.randomUUID().toString();
        OrderEvent event = new OrderEvent(orderId, "CREATED", amount);
        
        kafkaTemplate.send(KafkaTopicConfig.ORDER_TOPIC, orderId, event);
        log.info("Produced order event: {}", event.getOrderId());
        
        return event;
    }
}
