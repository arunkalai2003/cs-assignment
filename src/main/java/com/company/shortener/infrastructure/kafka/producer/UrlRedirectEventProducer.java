package com.company.shortener.infrastructure.kafka.producer;

import com.company.shortener.domain.exception.KafkaProcessingException;
import com.company.shortener.infrastructure.kafka.event.UrlRedirectAccessEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UrlRedirectEventProducer {

    private static final Logger log = LoggerFactory.getLogger(UrlRedirectEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    public UrlRedirectEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                   ObjectMapper objectMapper,
                                   @Value("${app.kafka.topics.url-redirect-events:url-shortener.redirect-events}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicName = topicName;
    }

    public void publish(UrlRedirectAccessEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event payload is required");
        }

        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicName, event.getEventId(), payload).get();
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize redirect event for shortCode {}", event.getShortCode(), e);
            throw new KafkaProcessingException("KAFKA_SERIALIZATION_FAILED", "Failed to serialize redirect event", e);
        } catch (Exception e) {
            log.error("Failed to publish redirect event for shortCode {}", event.getShortCode(), e);
            throw new KafkaProcessingException("KAFKA_PUBLISH_FAILED", "Kafka publish failed for redirect event", e);
        }
    }
}
