package com.company.shortener.application.service.impl;

import com.company.shortener.api.dto.request.CreateShortUrlRequest;
import com.company.shortener.api.dto.request.UpdateShortUrlRequest;
import com.company.shortener.api.dto.response.ShortUrlCreateResponse;
import com.company.shortener.api.dto.response.ShortUrlReadResponse;
import com.company.shortener.api.dto.response.ShortUrlUpdateResponse;
import com.company.shortener.domain.exception.ServiceException;
import com.company.shortener.domain.model.ShortUrlAnalyticsSummary;
import com.company.shortener.domain.model.ShortUrlInfo;
import com.company.shortener.domain.repository.ShortUrlAnalyticsSummaryRepository;
import com.company.shortener.domain.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private ShortUrlAnalyticsSummaryRepository analyticsSummaryRepository;

    @InjectMocks
    private UrlShortenerServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost:8080/");
    }

    @Test
    void createShortUrl_shouldReturnCreateResponse() {
        CreateShortUrlRequest request = new CreateShortUrlRequest(
                "https://example.com/products/123",
                null,
                "primary",
                "alice",
                "user-1",
                null
        );

        ShortUrlInfo saved = new ShortUrlInfo();
        saved.setId(101L);
        saved.setShortCode("AbCd1234");
        saved.setOriginalUrl("https://example.com/products/123");
        saved.setValue("http://localhost:8080/AbCd1234");
        saved.setAddedBy("alice");
        saved.setUserId("user-1");
        saved.setDateAdded(LocalDateTime.now());
        saved.setExpiryDate(null);
        saved.setActive(true);

        when(shortUrlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(shortUrlRepository.save(any(ShortUrlInfo.class))).thenReturn(saved);

        ShortUrlCreateResponse response = service.createShortUrl(request);

        assertNotNull(response);
        assertEquals(saved.getShortCode(), response.getShortCode());
        assertEquals(saved.getOriginalUrl(), response.getOriginalUrl());
        assertEquals("user-1", response.getUserId());
    }

    @Test
    void createShortUrl_shouldRejectInvalidUrl() {
        CreateShortUrlRequest request = new CreateShortUrlRequest(
                "not a valid url",
                null,
                "primary",
                "alice",
                "user-1",
                null
        );

        ServiceException ex = assertThrows(ServiceException.class, () -> service.createShortUrl(request));
        assertEquals("INVALID_URL", ex.getErrorCode());
    }

    @Test
    void getShortUrlByCode_shouldReturnUrlAndAnalyticsCount() {
        ShortUrlInfo url = new ShortUrlInfo();
        url.setId(10L);
        url.setShortCode("abc123");
        url.setOriginalUrl("https://example.com");
        url.setValue("http://localhost:8080/abc123");
        url.setAddedBy("alice");
        url.setModifiedBy("alice");
        url.setUserId("user-1");
        url.setDateAdded(LocalDateTime.now().minusDays(1));
        url.setDateModified(LocalDateTime.now());
        url.setActive(true);

        ShortUrlAnalyticsSummary summary = new ShortUrlAnalyticsSummary();
        summary.setTotalClicks(5L);
        summary.setUniqueVisitors(2L);
        summary.setLast7DaysClicks(5L);
        summary.setLastUpdated(LocalDateTime.now());

        when(shortUrlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));
        when(analyticsSummaryRepository.findByShortUrlId(10L)).thenReturn(Optional.of(summary));

        ShortUrlReadResponse response = service.getShortUrlByCode("abc123");

        assertNotNull(response);
        assertEquals("https://example.com", response.getOriginalUrl());
        assertEquals(5L, response.getTotalClicks());
    }

    @Test
    void updateShortUrl_shouldKeepShortCodeStableAndApplyChanges() {
        ShortUrlInfo existing = new ShortUrlInfo();
        existing.setId(20L);
        existing.setShortCode("stable12");
        existing.setOriginalUrl("https://old.example.com");
        existing.setValue("http://localhost:8080/stable12");
        existing.setAddedBy("alice");
        existing.setModifiedBy("alice");
        existing.setUserId("user-1");
        existing.setDateAdded(LocalDateTime.now().minusDays(2));
        existing.setActive(true);

        UpdateShortUrlRequest request = new UpdateShortUrlRequest();
        request.setOriginalUrl("https://new.example.com");
        request.setModifiedBy("bob");
        request.setValue("updated label");
        request.setExpiryDate(LocalDateTime.now().plusDays(7));

        when(shortUrlRepository.findByShortCode("stable12")).thenReturn(Optional.of(existing));
        when(shortUrlRepository.save(any(ShortUrlInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortUrlUpdateResponse response = service.updateShortUrl("stable12", request);

        assertEquals("stable12", response.getShortCode());
        assertEquals("https://new.example.com", response.getOriginalUrl());
        assertEquals("http://localhost:8080/stable12", response.getValue());
        assertEquals("bob", response.getModifiedBy());
    }

    @Test
    void deleteShortUrl_shouldHardDeleteRecord() {
        ShortUrlInfo existing = new ShortUrlInfo();
        existing.setId(30L);
        existing.setShortCode("delete99");
        existing.setOriginalUrl("https://example.com");
        existing.setAddedBy("alice");
        existing.setUserId("user-1");
        existing.setActive(true);

        when(shortUrlRepository.findByShortCode("delete99")).thenReturn(Optional.of(existing));

        assertDoesNotThrow(() -> service.deleteShortUrl("delete99", "admin"));
        verify(shortUrlRepository).delete(existing);
    }

    @Test
    void redirect_shouldReturnOriginalUrlWhenValid() {
        ShortUrlInfo url = new ShortUrlInfo();
        url.setId(40L);
        url.setShortCode("r1");
        url.setOriginalUrl("https://destination.example.com");
        url.setValue("http://localhost:8080/r1");
        url.setAddedBy("alice");
        url.setUserId("user-1");
        url.setActive(true);
        url.setExpiryDate(LocalDateTime.now().plusDays(1));

        when(shortUrlRepository.findByShortCode("r1")).thenReturn(Optional.of(url));

        String destination = service.redirect("r1");

        assertEquals("https://destination.example.com", destination);
    }

    @Test
    void redirect_shouldRejectExpiredUrl() {
        ShortUrlInfo url = new ShortUrlInfo();
        url.setId(50L);
        url.setShortCode("expired");
        url.setOriginalUrl("https://destination.example.com");
        url.setAddedBy("alice");
        url.setUserId("user-1");
        url.setActive(true);
        url.setExpiryDate(LocalDateTime.now().minusDays(1));

        when(shortUrlRepository.findByShortCode("expired")).thenReturn(Optional.of(url));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.redirect("expired"));
        assertEquals("EXPIRED", ex.getErrorCode());
    }
}
