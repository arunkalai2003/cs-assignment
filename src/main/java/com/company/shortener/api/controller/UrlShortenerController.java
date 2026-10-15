package com.company.shortener.api.controller;

import com.company.shortener.api.dto.request.CreateShortUrlRequest;
import com.company.shortener.api.dto.request.UpdateShortUrlRequest;
import com.company.shortener.api.dto.response.ShortUrlCreateResponse;
import com.company.shortener.api.dto.response.ShortUrlReadResponse;
import com.company.shortener.api.dto.response.ShortUrlUpdateResponse;
import com.company.shortener.application.service.UrlAnalyticsService;
import com.company.shortener.application.service.UrlShortenerService;
import com.company.shortener.domain.model.ShortUrlAnalyticsSummary;
import com.company.shortener.infrastructure.kafka.event.UrlRedirectAccessEvent;
import com.company.shortener.infrastructure.kafka.producer.UrlRedirectEventProducer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/urlshortener")
public class UrlShortenerController {

    private static final Logger log = LoggerFactory.getLogger(UrlShortenerController.class);

    private final UrlShortenerService urlShortenerService;
    private final UrlAnalyticsService urlAnalyticsService;
    private final UrlRedirectEventProducer redirectEventProducer;

    public UrlShortenerController(UrlShortenerService urlShortenerService,
                                 UrlAnalyticsService urlAnalyticsService,
                                 UrlRedirectEventProducer redirectEventProducer) {
        this.urlShortenerService = urlShortenerService;
        this.urlAnalyticsService = urlAnalyticsService;
        this.redirectEventProducer = redirectEventProducer;
    }

    @PostMapping
    public ResponseEntity<ShortUrlCreateResponse> createShortUrl(
            @Valid @RequestBody CreateShortUrlRequest request,
            @RequestHeader Map<String, String> headers) {

        Map<String, String> analyticsContext = extractAnalyticsContext(headers);
        log.info("Create short URL request analytics context: {}", analyticsContext);

        ShortUrlCreateResponse response = urlShortenerService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<ShortUrlReadResponse> getShortUrlByCode(
            @PathVariable @NotBlank(message = "shortCode must not be blank") String shortCode,
            @RequestHeader Map<String, String> headers) {

        Map<String, String> analyticsContext = extractAnalyticsContext(headers);
        log.info("Read short URL request analytics context: {}", analyticsContext);

        ShortUrlReadResponse response = urlShortenerService.getShortUrlByCode(shortCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/redirect/{shortCode}")
    public ResponseEntity<Void> redirectShortUrl(
            @PathVariable @NotBlank(message = "shortCode must not be blank") String shortCode,
            @RequestHeader(value = "X-Country", required = false) String xCountry,
            @RequestHeader(value = "X-Region", required = false) String xRegion,
            @RequestHeader(value = "Referer", required = false) String referer,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader Map<String, String> headers) {

        Map<String, String> analyticsContext = extractAnalyticsContext(headers);
        analyticsContext.putIfAbsent("X-Country", xCountry);
        analyticsContext.putIfAbsent("X-Region", xRegion);
        analyticsContext.putIfAbsent("Referer", referer);
        analyticsContext.putIfAbsent("User-Agent", userAgent);
        log.info("Redirect short URL analytics context: {}", analyticsContext);

        String targetUrl = urlShortenerService.redirect(shortCode);

        UrlRedirectAccessEvent event = new UrlRedirectAccessEvent(
                UUID.randomUUID().toString(),
                shortCode,
                LocalDateTime.now(),
                normalizeHeader(xCountry),
                normalizeHeader(xRegion),
                extractReferrerDomain(referer),
                classifyUserAgent(userAgent)
        );
        redirectEventProducer.publish(event);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(targetUrl))
                .build();
    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<ShortUrlUpdateResponse> updateShortUrl(
            @PathVariable @NotBlank(message = "shortCode must not be blank") String shortCode,
            @Valid @RequestBody UpdateShortUrlRequest request,
            @RequestHeader Map<String, String> headers) {

        Map<String, String> analyticsContext = extractAnalyticsContext(headers);
        log.info("Update short URL analytics context: {}", analyticsContext);

        ShortUrlUpdateResponse response = urlShortenerService.updateShortUrl(shortCode, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(
            @PathVariable @NotBlank(message = "shortCode must not be blank") String shortCode,
            @RequestParam(value = "deletedBy", required = false) String deletedBy,
            @RequestHeader Map<String, String> headers) {

        Map<String, String> analyticsContext = extractAnalyticsContext(headers);
        log.info("Delete short URL analytics context: {}", analyticsContext);

        urlShortenerService.deleteShortUrl(shortCode, deletedBy);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{code}/analytics")
    public ResponseEntity<ShortUrlAnalyticsSummary> getAnalyticsSummary(
            @PathVariable("code") @NotBlank(message = "code must not be blank") String code,
            @RequestHeader(value = "X-Country", required = false) String xCountry,
            @RequestHeader(value = "X-Region", required = false) String xRegion,
            @RequestHeader(value = "Referer", required = false) String referer,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader Map<String, String> headers) {

        Map<String, String> analyticsContext = extractAnalyticsContext(headers);
        analyticsContext.putIfAbsent("X-Country", xCountry);
        analyticsContext.putIfAbsent("X-Region", xRegion);
        analyticsContext.putIfAbsent("Referer", referer);
        analyticsContext.putIfAbsent("User-Agent", userAgent);
        log.info("Analytics request context: {}", analyticsContext);

        ShortUrlAnalyticsSummary summary = urlAnalyticsService.getAnalyticsSummary(code);
        return ResponseEntity.ok(summary);
    }

    private Map<String, String> extractAnalyticsContext(Map<String, String> headers) {
        Map<String, String> analyticsContext = new HashMap<>();
        if (headers == null) {
            return analyticsContext;
        }

        analyticsContext.put("X-Country", headers.getOrDefault("X-Country", ""));
        analyticsContext.put("X-Region", headers.getOrDefault("X-Region", ""));
        analyticsContext.put("Referer", headers.getOrDefault("Referer", ""));
        analyticsContext.put("User-Agent", headers.getOrDefault("User-Agent", ""));
        return analyticsContext;
    }

    private String normalizeHeader(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private String extractReferrerDomain(String referer) {
        if (referer == null || referer.isBlank()) {
            return "";
        }
        try {
            URI uri = URI.create(referer);
            return uri.getHost() == null ? referer : uri.getHost();
        } catch (Exception e) {
            return referer;
        }
    }

    private String classifyUserAgent(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        String value = userAgent.toLowerCase();
        if (value.contains("chrome")) {
            return "Chrome";
        }
        if (value.contains("firefox")) {
            return "Firefox";
        }
        if (value.contains("safari")) {
            return "Safari";
        }
        if (value.contains("edge")) {
            return "Edge";
        }
        if (value.contains("postman")) {
            return "Postman";
        }
        return "Other";
    }
}
