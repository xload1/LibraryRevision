package com.example.libraryrevision.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: body validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (var err : ex.getBindingResult().getFieldErrors()) {
            fields.put(err.getField(), message(err));
        }
        ApiError body = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .validation(fields)
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    private String message(FieldError e) {
        return e.getDefaultMessage();
    }

    // 400: incorrect JSON / field format
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleBadBody(HttpMessageNotReadableException ex,
                                                  HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST_BODY",
                "Malformed JSON or wrong field format", req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex,
                                                     HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION",
                ex.getMessage(), req);
    }

    // 409: uniqueness violation, etc.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException ex,
                                                   HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION",
                "Data integrity violation", req);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleRse(ResponseStatusException ex,
                                              HttpServletRequest req) {
        ex.getReason();
        ApiError body = ApiError.builder()
                .status(ex.getStatusCode().value())
                .code(ex.getReason())
                .message(ex.getMessage())
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    // 500: default
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR",
                "Unexpected server error", req);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String code, String msg, HttpServletRequest req) {
        ApiError body = ApiError.builder()
                .status(status.value())
                .code(code)
                .message(msg)
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
