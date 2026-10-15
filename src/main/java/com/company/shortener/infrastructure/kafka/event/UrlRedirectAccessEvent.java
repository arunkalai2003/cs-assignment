package com.company.shortener.infrastructure.kafka.event;

import java.time.LocalDateTime;

public class UrlRedirectAccessEvent {

    private String eventId;
    private String shortCode;
    private LocalDateTime accessedAt;
    private String country;
    private String region;
    private String referrerDomain;
    private String userAgentFamily;

    public UrlRedirectAccessEvent() {
    }

    public UrlRedirectAccessEvent(String eventId, String shortCode, LocalDateTime accessedAt,
                                 String country, String region, String referrerDomain,
                                 String userAgentFamily) {
        this.eventId = eventId;
        this.shortCode = shortCode;
        this.accessedAt = accessedAt;
        this.country = country;
        this.region = region;
        this.referrerDomain = referrerDomain;
        this.userAgentFamily = userAgentFamily;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public LocalDateTime getAccessedAt() {
        return accessedAt;
    }

    public void setAccessedAt(LocalDateTime accessedAt) {
        this.accessedAt = accessedAt;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getReferrerDomain() {
        return referrerDomain;
    }

    public void setReferrerDomain(String referrerDomain) {
        this.referrerDomain = referrerDomain;
    }

    public String getUserAgentFamily() {
        return userAgentFamily;
    }

    public void setUserAgentFamily(String userAgentFamily) {
        this.userAgentFamily = userAgentFamily;
    }
}
