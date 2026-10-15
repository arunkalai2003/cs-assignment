package com.company.shortener.api.dto.response;

import java.time.LocalDateTime;

public class ShortUrlCreateResponse {

    private Long id;
    private String shortCode;
    private String originalUrl;
    private String value;
    private String addedBy;
    private String userId;
    private LocalDateTime dateAdded;
    private LocalDateTime expiryDate;

    public ShortUrlCreateResponse() {
    }

    public ShortUrlCreateResponse(Long id, String shortCode, String originalUrl, String value,
                                 String addedBy, String userId, LocalDateTime dateAdded,
                                 LocalDateTime expiryDate) {
        this.id = id;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.value = value;
        this.addedBy = addedBy;
        this.userId = userId;
        this.dateAdded = dateAdded;
        this.expiryDate = expiryDate;
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

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
