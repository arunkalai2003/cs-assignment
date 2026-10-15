package com.company.shortener.application.service.impl;

import com.company.shortener.domain.model.ShortUrlAnalyticsSummary;
import com.company.shortener.domain.model.ShortUrlGeoData;
import com.company.shortener.domain.model.ShortUrlInfo;
import com.company.shortener.domain.repository.ShortUrlAnalyticsSummaryRepository;
import com.company.shortener.domain.repository.ShortUrlGeoDataRepository;
import com.company.shortener.domain.repository.ShortUrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlAnalyticsServiceImplTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private ShortUrlAnalyticsSummaryRepository summaryRepository;

    @Mock
    private ShortUrlGeoDataRepository geoDataRepository;

    @InjectMocks
    private UrlAnalyticsServiceImpl service;

    @Test
    void getAnalyticsSummary_shouldInitializeZeroWhenNoSummaryExists() {
        ShortUrlInfo url = new ShortUrlInfo();
        url.setId(2L);
        url.setShortCode("zero");
        url.setOriginalUrl("https://example.com");
        url.setAddedBy("alice");
        url.setUserId("user-1");
        url.setActive(true);

        when(shortUrlRepository.findByShortCode("zero")).thenReturn(Optional.of(url));
        when(summaryRepository.findByShortUrlId(2L)).thenReturn(Optional.empty());

        ShortUrlAnalyticsSummary summary = service.getAnalyticsSummary("zero");

        assertNotNull(summary);
        assertEquals(0L, summary.getTotalClicks());
        assertEquals(0L, summary.getUniqueVisitors());
        assertEquals(0L, summary.getLast7DaysClicks());
    }

    @Test
    void getGeoDataByShortCode_shouldReturnLocations() {
        ShortUrlInfo url = new ShortUrlInfo();
        url.setId(8L);
        url.setShortCode("geo");
        url.setOriginalUrl("https://example.com");
        url.setAddedBy("alice");
        url.setUserId("user-1");
        url.setActive(true);

        ShortUrlGeoData geo = new ShortUrlGeoData();
        geo.setId(11L);
        geo.setShortUrl(url);
        geo.setCountryCode("US");
        geo.setCountryName("United States");
        geo.setClickCount(7L);
        geo.setCapturedAt(LocalDateTime.now());

        List<ShortUrlGeoData> result = List.of(geo);

        assertEquals(1, result.size());
        assertEquals("US", result.get(0).getCountryCode());
    }
}
