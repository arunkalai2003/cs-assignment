package com.company.shortener.api.dto.error;

public class ErrorInfo {

    private String fieldName;
    private String message;

    public ErrorInfo() {
    }

    public ErrorInfo(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
