package com.company.shortener.api.dto.response;

import java.time.LocalDateTime;

public class ShortUrlReadResponse {

    private Long id;
    private String shortCode;
    private String originalUrl;
    private String value;
    private String addedBy;
    private String modifiedBy;
    private String userId;
    private LocalDateTime dateAdded;
    private LocalDateTime dateModified;
    private LocalDateTime expiryDate;
    private boolean active;
    private long totalClicks;

    public ShortUrlReadResponse() {
    }

    public ShortUrlReadResponse(Long id, String shortCode, String originalUrl, String value,
                               String addedBy, String modifiedBy, String userId,
                               LocalDateTime dateAdded, LocalDateTime dateModified,
                               LocalDateTime expiryDate, boolean active, long totalClicks) {
        this.id = id;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.value = value;
        this.addedBy = addedBy;
        this.modifiedBy = modifiedBy;
        this.userId = userId;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
        this.expiryDate = expiryDate;
        this.active = active;
        this.totalClicks = totalClicks;
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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(long totalClicks) {
        this.totalClicks = totalClicks;
    }
}
