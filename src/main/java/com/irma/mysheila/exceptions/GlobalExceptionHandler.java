package com.irma.mysheila.exceptions;


import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField()+": "+fe.getDefaultMessage()).toList();
        ApiError err = new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", "Validation failed", details);
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex) {
        ApiError err = new ApiError(Instant.now(), 400, "Bad Request", ex.getMessage(), List.of());
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex) {
        ApiError err = new ApiError(Instant.now(), 400, "Bad Request", ex.getMessage(), List.of());
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex) {
        ApiError err = new ApiError(Instant.now(), 500, "Internal Server Error", ex.getMessage(), List.of());
        return ResponseEntity.status(500).body(err);
    }
}
