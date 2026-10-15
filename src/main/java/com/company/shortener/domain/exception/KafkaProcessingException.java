package com.company.shortener.domain.exception;

public class KafkaProcessingException extends RuntimeException {

    private final String errorCode;

    public KafkaProcessingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
