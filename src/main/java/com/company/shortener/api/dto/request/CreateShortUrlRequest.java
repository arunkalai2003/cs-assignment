package com.company.shortener.api.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CreateShortUrlRequest {

    @NotBlank(message = "Original URL is required")
    @Size(max = 2048, message = "Original URL must not exceed 2048 characters")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Original URL must be a valid HTTP or FTP URL")
    private String originalUrl;

    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Short code may contain only letters and numbers")
    @Size(max = 32, message = "Short code must not exceed 32 characters")
    private String shortCode;

    @Size(max = 255, message = "Value must not exceed 255 characters")
    private String value;

    @NotBlank(message = "addedBy is required")
    @Size(max = 100, message = "addedBy must not exceed 100 characters")
    private String addedBy;

    @NotBlank(message = "userId is required")
    @Size(max = 64, message = "userId must not exceed 64 characters")
    private String userId;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;

    public CreateShortUrlRequest() {
    }

    public CreateShortUrlRequest(String originalUrl, String shortCode, String value,
                                String addedBy, String userId, LocalDateTime expiryDate) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.value = value;
        this.addedBy = addedBy;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
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

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
