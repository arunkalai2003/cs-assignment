package com.company.shortener.api.dto.error;

import java.time.LocalDateTime;

public class ApiErrorResponse {

    private LocalDateTime submitTime;
    private int status;
    private String message;
    private String path;
    private ErrorInfo[] details;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(LocalDateTime submitTime, int status, String message,
                           String path, ErrorInfo[] details) {
        this.submitTime = submitTime;
        this.status = status;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ErrorInfo[] getDetails() {
        return details;
    }

    public void setDetails(ErrorInfo[] details) {
        this.details = details;
    }
}
