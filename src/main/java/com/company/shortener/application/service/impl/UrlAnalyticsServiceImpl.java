package com.company.shortener.application.service.impl;

import com.company.shortener.application.service.UrlAnalyticsService;
import com.company.shortener.domain.exception.ServiceException;
import com.company.shortener.domain.model.ShortUrlAnalyticsSummary;
import com.company.shortener.domain.model.ShortUrlInfo;
import com.company.shortener.domain.repository.ShortUrlAnalyticsSummaryRepository;
import com.company.shortener.domain.repository.ShortUrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UrlAnalyticsServiceImpl implements UrlAnalyticsService {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlAnalyticsSummaryRepository analyticsSummaryRepository;

    public UrlAnalyticsServiceImpl(ShortUrlRepository shortUrlRepository,
                                    ShortUrlAnalyticsSummaryRepository analyticsSummaryRepository) {
        this.shortUrlRepository = shortUrlRepository;
        this.analyticsSummaryRepository = analyticsSummaryRepository;
    }

    @Override
    @Transactional
    public ShortUrlAnalyticsSummary getAnalyticsSummary(String shortCode) {
        String normalizedCode = normalize(shortCode);
        ShortUrlInfo shortUrl = shortUrlRepository.findByShortCode(normalizedCode)
                .orElseThrow(() -> new ServiceException("NOT_FOUND", "Short URL not found for code: " + normalizedCode));

        if (shortUrl.getExpiryDate() != null && shortUrl.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ServiceException("EXPIRED", "Short URL has expired.");
        }

        return analyticsSummaryRepository.findByShortUrlId(shortUrl.getId())
                .orElseGet(() -> new ShortUrlAnalyticsSummary(null, shortUrl, 0L, 0L, 0L, LocalDateTime.now()));
    }

    @Override
    @Transactional
    public ShortUrlAnalyticsSummary getSummaryForDisplay(String shortCode) {
        String normalizedCode = normalize(shortCode);
        ShortUrlInfo shortUrl = shortUrlRepository.findByShortCode(normalizedCode)
                .orElseThrow(() -> new ServiceException("NOT_FOUND", "Short URL not found for code: " + normalizedCode));

        if (shortUrl.getExpiryDate() != null && shortUrl.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ServiceException("EXPIRED", "Short URL has expired.");
        }

        return analyticsSummaryRepository.findByShortUrlId(shortUrl.getId())
                .orElseGet(() -> new ShortUrlAnalyticsSummary(null, shortUrl, 0L, 0L, 0L, LocalDateTime.now()));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
