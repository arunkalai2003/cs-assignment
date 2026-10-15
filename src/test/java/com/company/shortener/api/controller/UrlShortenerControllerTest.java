package com.company.shortener.api.controller;

import com.company.shortener.api.dto.request.CreateShortUrlRequest;
import com.company.shortener.api.dto.request.UpdateShortUrlRequest;
import com.company.shortener.api.dto.response.ShortUrlCreateResponse;
import com.company.shortener.api.dto.response.ShortUrlReadResponse;
import com.company.shortener.api.dto.response.ShortUrlUpdateResponse;
import com.company.shortener.application.service.UrlAnalyticsService;
import com.company.shortener.application.service.UrlShortenerService;
import com.company.shortener.domain.exception.ServiceException;
import com.company.shortener.domain.model.ShortUrlAnalyticsSummary;
import com.company.shortener.infrastructure.kafka.event.UrlRedirectAccessEvent;
import com.company.shortener.infrastructure.kafka.producer.UrlRedirectEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlShortenerController.class)
class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @MockBean
    private UrlAnalyticsService urlAnalyticsService;

    @MockBean
    private UrlRedirectEventProducer redirectEventProducer;

    @Test
    void createShortUrl_shouldReturn201() throws Exception {
        CreateShortUrlRequest request = new CreateShortUrlRequest(
                "https://example.com",
                null,
                "label",
                "alice",
                "user-1",
                null
        );

        ShortUrlCreateResponse response = new ShortUrlCreateResponse(
                1L,
                "abc123",
                "https://example.com",
                "http://localhost:8080/abc123",
                "alice",
                "user-1",
                LocalDateTime.now(),
                null
        );

        when(urlShortenerService.createShortUrl(any(CreateShortUrlRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/urlshortener")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"https://example.com\",\"addedBy\":\"alice\",\"userId\":\"user-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("abc123"));
    }

    @Test
    void invalidRequest_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/urlshortener")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getShortUrlByCode_shouldReturn200() throws Exception {
        ShortUrlReadResponse response = new ShortUrlReadResponse(
                1L,
                "abc123",
                "https://example.com",
                "http://localhost:8080/abc123",
                "alice",
                "alice",
                "user-1",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                true,
                3L
        );

        when(urlShortenerService.getShortUrlByCode("abc123")).thenReturn(response);

        mockMvc.perform(get("/api/v1/urlshortener/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"));
    }

    @Test
    void updateShortUrl_shouldReturn200() throws Exception {
        UpdateShortUrlRequest request = new UpdateShortUrlRequest();
        request.setOriginalUrl("https://updated.example.com");
        request.setModifiedBy("bob");

        ShortUrlUpdateResponse response = new ShortUrlUpdateResponse(
                1L,
                "abc123",
                "https://updated.example.com",
                "http://localhost:8080/abc123",
                "bob",
                LocalDateTime.now(),
                null,
                true
        );

        when(urlShortenerService.updateShortUrl(eq("abc123"), any(UpdateShortUrlRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/urlshortener/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"https://updated.example.com\",\"modifiedBy\":\"bob\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://updated.example.com"));
    }

    @Test
    void deleteShortUrl_shouldReturn204() throws Exception {
        doNothing().when(urlShortenerService).deleteShortUrl("abc123", null);

        mockMvc.perform(delete("/api/v1/urlshortener/abc123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void redirectShortUrl_shouldReturn302AndPublishEvent() throws Exception {
        when(urlShortenerService.redirect("abc123")).thenReturn("https://destination.example.com");

        mockMvc.perform(get("/api/v1/urlshortener/redirect/abc123")
                        .header("User-Agent", "Mozilla/5.0")
                        .header("X-Country", "US")
                        .header("X-Region", "TX")
                        .header("Referer", "https://referrer.example.com"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://destination.example.com"));

        verify(redirectEventProducer, times(1)).publish(any(UrlRedirectAccessEvent.class));
    }

    @Test
    void analytics_shouldReturn200() throws Exception {
        ShortUrlAnalyticsSummary summary = new ShortUrlAnalyticsSummary();
        summary.setTotalClicks(12L);
        summary.setUniqueVisitors(4L);
        summary.setLast7DaysClicks(12L);
        summary.setLastUpdated(LocalDateTime.now());

        when(urlAnalyticsService.getAnalyticsSummary("abc123")).thenReturn(summary);

        mockMvc.perform(get("/api/v1/urlshortener/abc123/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(12));
    }

    @Test
    void notFound_shouldReturn404() throws Exception {
        when(urlShortenerService.getShortUrlByCode("missing")).thenThrow(new ServiceException("NOT_FOUND", "Short URL not found"));

        mockMvc.perform(get("/api/v1/urlshortener/missing"))
                .andExpect(status().isNotFound());
    }
}
