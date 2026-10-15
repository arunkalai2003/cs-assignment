package com.company.shortener.infrastructure.kafka;

import com.company.shortener.domain.model.ProcessedRedirectEvent;
import com.company.shortener.domain.model.ShortUrlAnalyticsSummary;
import com.company.shortener.domain.model.ShortUrlGeoData;
import com.company.shortener.domain.model.ShortUrlInfo;
import com.company.shortener.domain.repository.ProcessedRedirectEventRepository;
import com.company.shortener.domain.repository.ShortUrlAnalyticsSummaryRepository;
import com.company.shortener.domain.repository.ShortUrlGeoDataRepository;
import com.company.shortener.domain.repository.ShortUrlRepository;
import com.company.shortener.infrastructure.kafka.consumer.UrlRedirectEventConsumer;
import com.company.shortener.infrastructure.kafka.event.UrlRedirectAccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlRedirectEventConsumerTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private ShortUrlAnalyticsSummaryRepository summaryRepository;

    @Mock
    private ShortUrlGeoDataRepository geoDataRepository;

    @Mock
    private ProcessedRedirectEventRepository processedEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UrlRedirectEventConsumer consumer;

    @Test
    void consume_shouldAggregateAnalyticsAndPersistGeoData() throws Exception {
        UrlRedirectAccessEvent event = new UrlRedirectAccessEvent(
                "evt-dup-1",
                "abc123",
                LocalDateTime.now(),
                "US",
                "TX",
                "example.com",
                "Chrome"
        );

        ShortUrlInfo shortUrl = new ShortUrlInfo();
        shortUrl.setId(99L);
        shortUrl.setShortCode("abc123");
        shortUrl.setOriginalUrl("https://example.com");
        shortUrl.setAddedBy("alice");
        shortUrl.setUserId("user-1");
        shortUrl.setActive(true);

        ShortUrlAnalyticsSummary summary = new ShortUrlAnalyticsSummary();
        summary.setId(1L);
        summary.setShortUrl(shortUrl);
        summary.setTotalClicks(2L);
        summary.setUniqueVisitors(1L);
        summary.setLast7DaysClicks(2L);
        summary.setLastUpdated(LocalDateTime.now().minusMinutes(5));

        ShortUrlGeoData geoData = new ShortUrlGeoData();
        geoData.setId(1L);
        geoData.setShortUrl(shortUrl);
        geoData.setCountryCode("US");
        geoData.setCountryName("United States");
        geoData.setClickCount(1L);
        geoData.setCapturedAt(LocalDateTime.now().minusMinutes(5));

        when(objectMapper.readValue(anyString(), eq(UrlRedirectAccessEvent.class))).thenReturn(event);
        when(processedEventRepository.existsByEventId("evt-dup-1")).thenReturn(false);
        when(shortUrlRepository.findByShortCode("abc123")).thenReturn(Optional.of(shortUrl));
        when(summaryRepository.findByShortUrlId(99L)).thenReturn(Optional.of(summary));
        when(geoDataRepository.findByShortUrlIdAndCountryCode(99L, "US")).thenReturn(Optional.of(geoData));

        consumer.consume("{\"eventId\":\"evt-dup-1\"}");

        verify(summaryRepository).save(any(ShortUrlAnalyticsSummary.class));
        verify(geoDataRepository).save(any(ShortUrlGeoData.class));
        verify(processedEventRepository).save(any(ProcessedRedirectEvent.class));
    }

    @Test
    void consume_shouldIgnoreDuplicateEventId() throws Exception {
        UrlRedirectAccessEvent event = new UrlRedirectAccessEvent(
                "evt-dupe",
                "abc123",
                LocalDateTime.now(),
                "US",
                "TX",
                "example.com",
                "Chrome"
        );

        when(objectMapper.readValue(anyString(), eq(UrlRedirectAccessEvent.class))).thenReturn(event);
        when(processedEventRepository.existsByEventId("evt-dupe")).thenReturn(true);

        consumer.consume("{\"eventId\":\"evt-dupe\"}");

        verify(summaryRepository, never()).save(any());
        verify(geoDataRepository, never()).save(any());
    }
}
