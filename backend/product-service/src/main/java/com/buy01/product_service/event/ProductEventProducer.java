package com.buy01.product_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {

    // Spring Boot automatically configures this template based on your application.yml
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    private static final String TOPIC = "product-deletion-topic";

    public void publishProductDeletedEvent(String mediaId) {
        log.info("📢 Publishing product deletion event to Kafka for media ID: {}", mediaId);
        // We send the mediaId as a simple string message to the topic
        kafkaTemplate.send(TOPIC, mediaId);
    }
}