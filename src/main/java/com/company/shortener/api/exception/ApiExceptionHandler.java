package com.company.shortener.api.exception;

import com.company.shortener.api.dto.error.ApiErrorResponse;
import com.company.shortener.api.dto.error.ErrorInfo;
import com.company.shortener.domain.exception.KafkaProcessingException;
import com.company.shortener.domain.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleServiceException(ServiceException ex,
                                                                   HttpServletRequest request) {
        HttpStatus status = mapStatus(ex.getErrorCode());
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI(),
                new ErrorInfo[]{new ErrorInfo("errorCode", ex.getErrorCode())}
        );
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                        HttpServletRequest request) {
        ErrorInfo[] details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorInfo(fieldError.getField(), fieldError.getDefaultMessage()))
                .toArray(ErrorInfo[]::new);

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getRequestURI(),
                details
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                     HttpServletRequest request) {
        ErrorInfo[] details = ex.getConstraintViolations().stream()
                .map(violation -> new ErrorInfo(violation.getPropertyPath().toString(), violation.getMessage()))
                .toArray(ErrorInfo[]::new);

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Constraint validation failed",
                request.getRequestURI(),
                details
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(KafkaProcessingException.class)
    public ResponseEntity<ApiErrorResponse> handleKafkaProcessing(KafkaProcessingException ex,
                                                                HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_GATEWAY.value(),
                "Kafka processing failed",
                request.getRequestURI(),
                new ErrorInfo[]{new ErrorInfo(ex.getErrorCode(), ex.getMessage())}
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiErrorResponse> handleBadInput(RuntimeException ex,
                                                          HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                new ErrorInfo[]{new ErrorInfo("request", ex.getMessage())}
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex,
                                                                 HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected server error",
                request.getRequestURI(),
                new ErrorInfo[]{new ErrorInfo("exception", ex.getMessage())}
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private HttpStatus mapStatus(String errorCode) {
        if (errorCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        switch (errorCode) {
            case "INVALID_REQUEST":
            case "INVALID_URL":
            case "INVALID_SHORT_CODE":
            case "INVALID_EXPIRY_DATE":
            case "DUPLICATE_SHORT_CODE":
                return HttpStatus.BAD_REQUEST;
            case "NOT_FOUND":
                return HttpStatus.NOT_FOUND;
            case "EXPIRED":
                return HttpStatus.GONE;
            case "INACTIVE":
                return HttpStatus.CONFLICT;
            case "KAFKA_PUBLISH_FAILED":
            case "KAFKA_SERIALIZATION_FAILED":
                return HttpStatus.BAD_GATEWAY;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
