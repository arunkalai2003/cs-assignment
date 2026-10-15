package com.company.shortener.infrastructure.kafka.consumer;

import com.company.shortener.domain.model.ProcessedRedirectEvent;
import com.company.shortener.domain.model.ShortUrlAnalyticsSummary;
import com.company.shortener.domain.model.ShortUrlGeoData;
import com.company.shortener.domain.model.ShortUrlInfo;
import com.company.shortener.domain.repository.ProcessedRedirectEventRepository;
import com.company.shortener.domain.repository.ShortUrlAnalyticsSummaryRepository;
import com.company.shortener.domain.repository.ShortUrlGeoDataRepository;
import com.company.shortener.domain.repository.ShortUrlRepository;
import com.company.shortener.infrastructure.kafka.event.UrlRedirectAccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class UrlRedirectEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UrlRedirectEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlAnalyticsSummaryRepository summaryRepository;
    private final ShortUrlGeoDataRepository geoDataRepository;
    private final ProcessedRedirectEventRepository processedEventRepository;

    public UrlRedirectEventConsumer(ObjectMapper objectMapper,
                                   ShortUrlRepository shortUrlRepository,
                                   ShortUrlAnalyticsSummaryRepository summaryRepository,
                                   ShortUrlGeoDataRepository geoDataRepository,
                                   ProcessedRedirectEventRepository processedEventRepository) {
        this.objectMapper = objectMapper;
        this.shortUrlRepository = shortUrlRepository;
        this.summaryRepository = summaryRepository;
        this.geoDataRepository = geoDataRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = "${app.kafka.topics.url-redirect-events}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consume(String payload) {
        if (payload == null || payload.isBlank()) {
            return;
        }

        UrlRedirectAccessEvent event;
        try {
            event = objectMapper.readValue(payload, UrlRedirectAccessEvent.class);
        } catch (Exception e) {
            log.warn("Failed to deserialize redirect analytics event: {}", payload, e);
            return;
        }

        if (event == null || event.getEventId() == null || event.getShortCode() == null) {
            return;
        }

        if (processedEventRepository.existsByEventId(event.getEventId())) {
            log.info("Duplicate redirect event ignored: {}", event.getEventId());
            return;
        }

        Optional<ShortUrlInfo> shortUrlOptional = shortUrlRepository.findByShortCode(event.getShortCode());
        if (shortUrlOptional.isEmpty()) {
            log.warn("Received analytics event for unknown shortCode: {}", event.getShortCode());
            persistProcessedEvent(event.getEventId());
            return;
        }

        ShortUrlInfo shortUrl = shortUrlOptional.get();
        ShortUrlAnalyticsSummary summary = summaryRepository.findByShortUrlId(shortUrl.getId())
                .orElseGet(() -> {
                    ShortUrlAnalyticsSummary newSummary = new ShortUrlAnalyticsSummary();
                    newSummary.setShortUrl(shortUrl);
                    newSummary.setTotalClicks(0L);
                    newSummary.setUniqueVisitors(0L);
                    newSummary.setLast7DaysClicks(0L);
                    newSummary.setLastUpdated(LocalDateTime.now());
                    return newSummary;
                });

        summary.setTotalClicks(summary.getTotalClicks() + 1L);
        summary.setLastUpdated(event.getAccessedAt() != null ? event.getAccessedAt() : LocalDateTime.now());
        summary.setLast7DaysClicks(summary.getLast7DaysClicks() + 1L);
        summaryRepository.save(summary);

        String country = normalizeCountry(event.getCountry());
        if (!country.isBlank()) {
            ShortUrlGeoData geoData = geoDataRepository.findByShortUrlIdAndCountryCode(shortUrl.getId(), country)
                    .orElseGet(() -> {
                        ShortUrlGeoData newGeo = new ShortUrlGeoData();
                        newGeo.setShortUrl(shortUrl);
                        newGeo.setCountryCode(country);
                        newGeo.setCountryName(event.getCountry());
                        newGeo.setClickCount(0L);
                        newGeo.setCapturedAt(LocalDateTime.now());
                        return newGeo;
                    });

            geoData.setClickCount(geoData.getClickCount() + 1L);
            geoData.setCapturedAt(event.getAccessedAt() != null ? event.getAccessedAt() : LocalDateTime.now());
            geoDataRepository.save(geoData);
        }

        persistProcessedEvent(event.getEventId());
    }

    private void persistProcessedEvent(String eventId) {
        ProcessedRedirectEvent processedRedirectEvent = new ProcessedRedirectEvent();
        processedRedirectEvent.setEventId(eventId);
        processedRedirectEvent.setProcessedAt(LocalDateTime.now());
        processedEventRepository.save(processedRedirectEvent);
    }

    private String normalizeCountry(String country) {
        if (country == null) {
            return "";
        }
        String normalized = country.trim();
        if (normalized.length() == 2) {
            return normalized.toUpperCase();
        }
        return normalized;
    }
}
