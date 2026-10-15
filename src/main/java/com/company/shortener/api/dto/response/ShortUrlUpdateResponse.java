package com.company.shortener.api.dto.response;

import java.time.LocalDateTime;

public class ShortUrlUpdateResponse {

    private Long id;
    private String shortCode;
    private String originalUrl;
    private String value;
    private String modifiedBy;
    private LocalDateTime dateModified;
    private LocalDateTime expiryDate;
    private boolean active;

    public ShortUrlUpdateResponse() {
    }

    public ShortUrlUpdateResponse(Long id, String shortCode, String originalUrl, String value,
                                 String modifiedBy, LocalDateTime dateModified,
                                 LocalDateTime expiryDate, boolean active) {
        this.id = id;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.value = value;
        this.modifiedBy = modifiedBy;
        this.dateModified = dateModified;
        this.expiryDate = expiryDate;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
