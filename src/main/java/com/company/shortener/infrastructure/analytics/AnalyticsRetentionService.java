package com.company.shortener.infrastructure.analytics;

import com.company.shortener.domain.repository.ProcessedRedirectEventRepository;
import com.company.shortener.domain.repository.ShortUrlAnalyticsSummaryRepository;
import com.company.shortener.domain.repository.ShortUrlGeoDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AnalyticsRetentionService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsRetentionService.class);

    private final ShortUrlAnalyticsSummaryRepository summaryRepository;
    private final ShortUrlGeoDataRepository geoDataRepository;
    private final ProcessedRedirectEventRepository processedEventRepository;

    @Value("${app.analytics.retention-days:60}")
    private int retentionDays;

    public AnalyticsRetentionService(ShortUrlAnalyticsSummaryRepository summaryRepository,
                                    ShortUrlGeoDataRepository geoDataRepository,
                                    ProcessedRedirectEventRepository processedEventRepository) {
        this.summaryRepository = summaryRepository;
        this.geoDataRepository = geoDataRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Scheduled(cron = "${app.analytics.retention-cron:0 0 2 * * ?}")
    @Transactional
    public void cleanupExpiredAnalytics() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);

        int deletedSummaries = summaryRepository.deleteByLastUpdatedBefore(cutoff);
        int deletedGeoData = geoDataRepository.deleteByCapturedAtBefore(cutoff);
        int deletedProcessedEvents = processedEventRepository.deleteByProcessedAtBefore(cutoff);

        log.info("Analytics retention cleanup completed: deleted summaries={}, geoRecords={}, processedEvents={}, cutoff={}",
                deletedSummaries, deletedGeoData, deletedProcessedEvents, cutoff);
    }
}
