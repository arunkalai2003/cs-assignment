package com.company.shortener.application.service;

import com.company.shortener.domain.model.ShortUrlAnalyticsSummary;
import com.company.shortener.domain.exception.ServiceException;

public interface UrlAnalyticsService {

    /**
     * Retrieves the analytics summary for a short URL.
     *
     * Expected behavior:
     * - returns the aggregate metrics for the matching short code
     * - creates an empty summary row if the service is configured to initialize analytics lazily
     * - throws ServiceException with NOT_FOUND when the short URL does not exist
     * - throws ServiceException with EXPIRED when the short URL is expired and analytics are blocked
     */
    ShortUrlAnalyticsSummary getAnalyticsSummary(String shortCode);

    /**
     * Returns analytics for a short URL that is ready to be displayed in a dashboard.
     *
     * Expected behavior:
     * - includes total clicks, unique visitors, and trailing-period metrics
     * - should not fail for a valid short code with no prior clicks; the summary should be zero-filled
     * - should validate short-code existence before returning data
     */
    ShortUrlAnalyticsSummary getSummaryForDisplay(String shortCode);
}
