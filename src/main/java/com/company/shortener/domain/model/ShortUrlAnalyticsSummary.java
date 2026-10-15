package com.company.shortener.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_url_analytics_summary")
public class ShortUrlAnalyticsSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "short_url_id", nullable = false)
    @NotNull
    private ShortUrlInfo shortUrl;

    @Column(name = "total_clicks", nullable = false)
    @Min(0)
    private long totalClicks = 0L;

    @Column(name = "unique_visitors", nullable = false)
    @Min(0)
    private long uniqueVisitors = 0L;

    @Column(name = "last_7_days_clicks", nullable = false)
    @Min(0)
    private long last7DaysClicks = 0L;

    @Column(name = "last_updated", nullable = false)
    @NotNull
    private LocalDateTime lastUpdated;

    public ShortUrlAnalyticsSummary() {
    }

    public ShortUrlAnalyticsSummary(Long id, ShortUrlInfo shortUrl, long totalClicks,
                                   long uniqueVisitors, long last7DaysClicks,
                                   LocalDateTime lastUpdated) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.totalClicks = totalClicks;
        this.uniqueVisitors = uniqueVisitors;
        this.last7DaysClicks = last7DaysClicks;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ShortUrlInfo getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(ShortUrlInfo shortUrl) {
        this.shortUrl = shortUrl;
    }

    public long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public long getUniqueVisitors() {
        return uniqueVisitors;
    }

    public void setUniqueVisitors(long uniqueVisitors) {
        this.uniqueVisitors = uniqueVisitors;
    }

    public long getLast7DaysClicks() {
        return last7DaysClicks;
    }

    public void setLast7DaysClicks(long last7DaysClicks) {
        this.last7DaysClicks = last7DaysClicks;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
