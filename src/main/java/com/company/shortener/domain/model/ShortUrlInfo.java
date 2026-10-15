package com.company.shortener.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "short_url_info")
public class ShortUrlInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 32)
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]+$")
    private String shortCode;

    @Column(name = "original_url", nullable = false, length = 2048)
    @NotBlank
    @Size(max = 2048)
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Original URL must be a valid HTTP or FTP URL")
    private String originalUrl;

    @Column(name = "value", length = 255)
    @Size(max = 255)
    private String value;

    @Column(name = "added_by", nullable = false, length = 100)
    @NotBlank
    private String addedBy;

    @Column(name = "date_added", nullable = false, updatable = false)
    private LocalDateTime dateAdded;

    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @Column(name = "date_modified")
    private LocalDateTime dateModified;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "user_id", nullable = false, length = 64)
    @NotBlank
    private String userId;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "shortUrl", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShortUrlAnalyticsSummary> analyticsSummaries = new ArrayList<>();

    @OneToMany(mappedBy = "shortUrl", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShortUrlGeoData> geoData = new ArrayList<>();

    public ShortUrlInfo() {
    }

    public ShortUrlInfo(Long id, String shortCode, String originalUrl, String value,
                        String addedBy, LocalDateTime dateAdded, String modifiedBy,
                        LocalDateTime dateModified, LocalDateTime expiryDate, String userId,
                        boolean active) {
        this.id = id;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.value = value;
        this.addedBy = addedBy;
        this.dateAdded = dateAdded;
        this.modifiedBy = modifiedBy;
        this.dateModified = dateModified;
        this.expiryDate = expiryDate;
        this.userId = userId;
        this.active = active;
    }

    @PrePersist
    public void prePersist() {
        if (dateAdded == null) {
            dateAdded = LocalDateTime.now();
        }
        if (modifiedBy == null) {
            modifiedBy = addedBy;
        }
        if (dateModified == null) {
            dateModified = dateAdded;
        }
    }

    @PreUpdate
    public void preUpdate() {
        dateModified = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<ShortUrlAnalyticsSummary> getAnalyticsSummaries() {
        return analyticsSummaries;
    }

    public void setAnalyticsSummaries(List<ShortUrlAnalyticsSummary> analyticsSummaries) {
        this.analyticsSummaries = analyticsSummaries;
    }

    public List<ShortUrlGeoData> getGeoData() {
        return geoData;
    }

    public void setGeoData(List<ShortUrlGeoData> geoData) {
        this.geoData = geoData;
    }
}
