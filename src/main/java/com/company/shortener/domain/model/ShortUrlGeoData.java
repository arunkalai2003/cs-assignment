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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_url_geo_data")
public class ShortUrlGeoData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "short_url_id", nullable = false)
    @NotNull
    private ShortUrlInfo shortUrl;

    @Column(name = "country_code", nullable = false, length = 2)
    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$")
    private String countryCode;

    @Column(name = "country_name", length = 100)
    private String countryName;

    @Column(name = "click_count", nullable = false)
    @Min(0)
    private long clickCount = 0L;

    @Column(name = "captured_at", nullable = false)
    @NotNull
    private LocalDateTime capturedAt;

    public ShortUrlGeoData() {
    }

    public ShortUrlGeoData(Long id, ShortUrlInfo shortUrl, String countryCode,
                           String countryName, long clickCount, LocalDateTime capturedAt) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.clickCount = clickCount;
        this.capturedAt = capturedAt;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }

    public LocalDateTime getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(LocalDateTime capturedAt) {
        this.capturedAt = capturedAt;
    }
}
