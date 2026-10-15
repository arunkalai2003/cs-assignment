package com.company.shortener.api.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class UpdateShortUrlRequest {

    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Short code may contain only letters and numbers")
    @Size(max = 32, message = "Short code must not exceed 32 characters")
    private String shortCode;

    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Original URL must be a valid HTTP or FTP URL")
    @Size(max = 2048, message = "Original URL must not exceed 2048 characters")
    private String originalUrl;

    @Size(max = 255, message = "Value must not exceed 255 characters")
    private String value;

    @Size(max = 100, message = "modifiedBy must not exceed 100 characters")
    private String modifiedBy;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;

    public UpdateShortUrlRequest() {
    }

    public UpdateShortUrlRequest(String shortCode, String originalUrl, String value,
                                String modifiedBy, LocalDateTime expiryDate) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.value = value;
        this.modifiedBy = modifiedBy;
        this.expiryDate = expiryDate;
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

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
