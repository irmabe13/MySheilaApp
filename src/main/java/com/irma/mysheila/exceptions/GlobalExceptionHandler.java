package com.irma.mysheila.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> handleUserExists(UserAlreadyExistException ex) {
        return buildErrorResponse(ex.getMessage(), "Cette adresse email est déjà utilisée", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UsernameNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), "Cet utilisateur n'existe pas", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ IllegalArgumentException.class, DateTimeParseException.class })
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(),"Mauvaise requête", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return buildErrorResponse(ex.getMessage(), "Une erreur inattendue est survenue", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String error, String details, HttpStatus status) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "error", error,
                "details", details,
                "status", status.value()
        );
        return new ResponseEntity<>(body, status);
    }
}

