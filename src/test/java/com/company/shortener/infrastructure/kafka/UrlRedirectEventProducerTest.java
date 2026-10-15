package com.company.shortener.infrastructure.kafka;

import com.company.shortener.domain.exception.KafkaProcessingException;
import com.company.shortener.infrastructure.kafka.event.UrlRedirectAccessEvent;
import com.company.shortener.infrastructure.kafka.producer.UrlRedirectEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlRedirectEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UrlRedirectEventProducer producer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(producer, "topicName", "url-shortener.redirect-events");
    }

    @Test
    void publish_shouldSendEventToKafka() throws Exception {
        UrlRedirectAccessEvent event = new UrlRedirectAccessEvent(
                "evt-1",
                "abc123",
                LocalDateTime.now(),
                "US",
                "TX",
                "example.com",
                "Chrome"
        );

        when(objectMapper.writeValueAsString(event)).thenReturn("{\"shortCode\":\"abc123\"}");
        when(kafkaTemplate.send("url-shortener.redirect-events", "evt-1", "{\"shortCode\":\"abc123\"}"))
                .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> producer.publish(event));
        verify(kafkaTemplate).send("url-shortener.redirect-events", "evt-1", "{\"shortCode\":\"abc123\"}");
    }

    @Test
    void publish_shouldThrowKafkaProcessingExceptionWhenSerializationFails() throws Exception {
        UrlRedirectAccessEvent event = new UrlRedirectAccessEvent(
                "evt-2",
                "abc123",
                LocalDateTime.now(),
                "US",
                "TX",
                "example.com",
                "Chrome"
        );

        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("boom") {
        });

        KafkaProcessingException ex = assertThrows(KafkaProcessingException.class, () -> producer.publish(event));
        assertEquals("KAFKA_SERIALIZATION_FAILED", ex.getErrorCode());
    }
}
