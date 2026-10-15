package com.company.shortener.application.service.impl;

import com.company.shortener.api.dto.request.CreateShortUrlRequest;
import com.company.shortener.api.dto.request.UpdateShortUrlRequest;
import com.company.shortener.api.dto.response.ShortUrlCreateResponse;
import com.company.shortener.api.dto.response.ShortUrlReadResponse;
import com.company.shortener.api.dto.response.ShortUrlUpdateResponse;
import com.company.shortener.application.service.UrlShortenerService;
import com.company.shortener.domain.exception.ServiceException;
import com.company.shortener.domain.model.ShortUrlAnalyticsSummary;
import com.company.shortener.domain.model.ShortUrlInfo;
import com.company.shortener.domain.repository.ShortUrlAnalyticsSummaryRepository;
import com.company.shortener.domain.repository.ShortUrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private static final int SHORT_CODE_LENGTH = 8;
    private static final String ALLOWED_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlAnalyticsSummaryRepository analyticsSummaryRepository;

    @Value("${app.base-url:http://localhost:8080/}")
    private String baseUrl;

    public UrlShortenerServiceImpl(ShortUrlRepository shortUrlRepository,
                                     ShortUrlAnalyticsSummaryRepository analyticsSummaryRepository) {
        this.shortUrlRepository = shortUrlRepository;
        this.analyticsSummaryRepository = analyticsSummaryRepository;
    }

    @Override
    @Transactional
    public ShortUrlCreateResponse createShortUrl(CreateShortUrlRequest request) {
        if (request == null) {
            throw new ServiceException("INVALID_REQUEST", "Request body is required.");
        }

        String originalUrl = normalize(request.getOriginalUrl());
        String addedBy = normalize(request.getAddedBy());
        String userId = normalize(request.getUserId());

        if (originalUrl.isBlank() || !isValidUrl(originalUrl)) {
            throw new ServiceException("INVALID_URL", "Original URL is required and must be a valid HTTP/FTP URL.");
        }

        if (addedBy.isBlank()) {
            throw new ServiceException("INVALID_REQUEST", "addedBy is required.");
        }

        if (userId.isBlank()) {
            throw new ServiceException("INVALID_REQUEST", "userId is required.");
        }

        String shortCode = normalize(request.getShortCode());
        if (shortCode.isBlank()) {
            shortCode = generateUniqueShortCode();
        }

        if (!shortCode.matches("^[A-Za-z0-9]+$") || shortCode.length() > 32) {
            throw new ServiceException("INVALID_SHORT_CODE", "Short code must contain only letters and numbers and be at most 32 characters long.");
        }

        if (shortUrlRepository.existsByShortCode(shortCode)) {
            throw new ServiceException("DUPLICATE_SHORT_CODE", "Short code already exists.");
        }

        LocalDateTime expiryDate = request.getExpiryDate();
        if (expiryDate != null && !expiryDate.isAfter(LocalDateTime.now())) {
            throw new ServiceException("INVALID_EXPIRY_DATE", "Expiry date must be in the future.");
        }

        ShortUrlInfo shortUrl = new ShortUrlInfo();
        shortUrl.setShortCode(shortCode);
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setValue(buildShortUrlValue(shortCode));
        shortUrl.setAddedBy(addedBy);
        shortUrl.setModifiedBy(addedBy);
        shortUrl.setUserId(userId);
        shortUrl.setExpiryDate(expiryDate);
        shortUrl.setActive(true);

        ShortUrlInfo saved = shortUrlRepository.save(shortUrl);

        return new ShortUrlCreateResponse(
                saved.getId(),
                saved.getShortCode(),
                saved.getOriginalUrl(),
                saved.getValue(),
                saved.getAddedBy(),
                saved.getUserId(),
                saved.getDateAdded(),
                saved.getExpiryDate()
        );
    }

    @Override
    @Transactional
    public ShortUrlReadResponse getShortUrlByCode(String shortCode) {
        String normalizedCode = normalize(shortCode);

        ShortUrlInfo shortUrl = shortUrlRepository.findByShortCode(normalizedCode)
                .orElseThrow(() -> new ServiceException("NOT_FOUND", "Short URL not found for code: " + normalizedCode));

        if (!shortUrl.isActive()) {
            throw new ServiceException("INACTIVE", "Short URL is inactive.");
        }

        if (isExpired(shortUrl)) {
            throw new ServiceException("EXPIRED", "Short URL has expired.");
        }

        long totalClicks = analyticsSummaryRepository.findByShortUrlId(shortUrl.getId())
                .map(ShortUrlAnalyticsSummary::getTotalClicks)
                .orElse(0L);

        return new ShortUrlReadResponse(
                shortUrl.getId(),
                shortUrl.getShortCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getValue(),
                shortUrl.getAddedBy(),
                shortUrl.getModifiedBy(),
                shortUrl.getUserId(),
                shortUrl.getDateAdded(),
                shortUrl.getDateModified(),
                shortUrl.getExpiryDate(),
                shortUrl.isActive(),
                totalClicks
        );
    }

    @Override
    @Transactional
    public ShortUrlUpdateResponse updateShortUrl(String shortCode, UpdateShortUrlRequest request) {
        if (request == null) {
            throw new ServiceException("INVALID_REQUEST", "Update request body is required.");
        }

        String normalizedCode = normalize(shortCode);
        ShortUrlInfo existing = shortUrlRepository.findByShortCode(normalizedCode)
                .orElseThrow(() -> new ServiceException("NOT_FOUND", "Short URL not found for code: " + normalizedCode));

        if (!existing.isActive()) {
            throw new ServiceException("INACTIVE", "Cannot update an inactive short URL.");
        }

        if (request.getShortCode() != null && !normalize(request.getShortCode()).isBlank()) {
            String requestedCode = normalize(request.getShortCode());
            if (!requestedCode.equalsIgnoreCase(existing.getShortCode())) {
                throw new ServiceException("INVALID_REQUEST", "Short code is stable and cannot be changed after creation.");
            }
        }

        if (request.getOriginalUrl() != null && !normalize(request.getOriginalUrl()).isBlank()) {
            String originalUrl = normalize(request.getOriginalUrl());
            if (!isValidUrl(originalUrl)) {
                throw new ServiceException("INVALID_URL", "Original URL is invalid.");
            }
            existing.setOriginalUrl(originalUrl);
        }

        if (request.getValue() != null && !normalize(request.getValue()).isBlank()) {
            existing.setValue(normalize(request.getValue()));
        }

        if (request.getExpiryDate() != null) {
            if (!request.getExpiryDate().isAfter(LocalDateTime.now())) {
                throw new ServiceException("INVALID_EXPIRY_DATE", "Expiry date must be in the future.");
            }
            existing.setExpiryDate(request.getExpiryDate());
        }

        String modifiedBy = normalize(request.getModifiedBy());
        if (!modifiedBy.isBlank()) {
            existing.setModifiedBy(modifiedBy);
        }

        existing.setValue(buildShortUrlValue(existing.getShortCode()));
        ShortUrlInfo saved = shortUrlRepository.save(existing);

        return new ShortUrlUpdateResponse(
                saved.getId(),
                saved.getShortCode(),
                saved.getOriginalUrl(),
                saved.getValue(),
                saved.getModifiedBy(),
                saved.getDateModified(),
                saved.getExpiryDate(),
                saved.isActive()
        );
    }

    @Override
    @Transactional
    public void deleteShortUrl(String shortCode, String deletedBy) {
        String normalizedCode = normalize(shortCode);
        ShortUrlInfo shortUrl = shortUrlRepository.findByShortCode(normalizedCode)
                .orElseThrow(() -> new ServiceException("NOT_FOUND", "Short URL not found for code: " + normalizedCode));

        shortUrlRepository.delete(shortUrl);
    }

    @Override
    @Transactional
    public String redirect(String shortCode) {
        String normalizedCode = normalize(shortCode);
        ShortUrlInfo shortUrl = shortUrlRepository.findByShortCode(normalizedCode)
                .orElseThrow(() -> new ServiceException("NOT_FOUND", "Short URL not found for code: " + normalizedCode));

        if (!shortUrl.isActive()) {
            throw new ServiceException("INACTIVE", "Short URL is inactive.");
        }

        if (isExpired(shortUrl)) {
            throw new ServiceException("EXPIRED", "Short URL has expired.");
        }

        return shortUrl.getOriginalUrl();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme) || "ftp".equalsIgnoreCase(scheme))
                    && host != null && !host.isBlank();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private boolean isExpired(ShortUrlInfo shortUrl) {
        return shortUrl.getExpiryDate() != null && shortUrl.getExpiryDate().isBefore(LocalDateTime.now());
    }

    private String generateUniqueShortCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(ALLOWED_CODE_CHARS.length());
            builder.append(ALLOWED_CODE_CHARS.charAt(index));
        }

        String candidate = builder.toString();
        if (shortUrlRepository.existsByShortCode(candidate)) {
            return generateUniqueShortCode();
        }
        return candidate;
    }

    private String buildShortUrlValue(String shortCode) {
        return baseUrl.endsWith("/") ? baseUrl + shortCode : baseUrl + "/" + shortCode;
    }
}
