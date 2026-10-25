package com.webcodein.concurrency.web;

import com.webcodein.concurrency.context.RequestContextScope;
import com.webcodein.concurrency.exception.FraudDetectedException;
import com.webcodein.concurrency.exception.InventoryUnavailableException;
import com.webcodein.concurrency.exception.PaymentDeclinedException;
import com.webcodein.concurrency.model.RequestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FraudDetectedException.class)
    public ResponseEntity<Map<String, Object>> handleFraudDetected(FraudDetectedException ex) {
        RequestContext context = RequestContextScope.get();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", "FRAUD_DETECTED",
                "message", ex.getMessage(),
                "riskScore", ex.getRiskScore(),
                "correlationId", context.correlationId(),
                "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(PaymentDeclinedException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentDeclined(PaymentDeclinedException ex) {
        RequestContext context = RequestContextScope.get();
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(Map.of(
                "error", "PAYMENT_DECLINED",
                "message", ex.getMessage(),
                "correlationId", context.correlationId(),
                "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(InventoryUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleInventoryUnavailable(InventoryUnavailableException ex) {
        RequestContext context = RequestContextScope.get();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "INVENTORY_UNAVAILABLE",
                "message", ex.getMessage(),
                "correlationId", context.correlationId(),
                "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        RequestContext context = RequestContextScope.get();
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (msg1, msg2) -> msg1
                ));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "VALIDATION_FAILED",
                "fieldErrors", fieldErrors,
                "correlationId", context.correlationId(),
                "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        RequestContext context = RequestContextScope.get();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
                "correlationId", context.correlationId(),
                "timestamp", Instant.now().toString()
        ));
    }
}
