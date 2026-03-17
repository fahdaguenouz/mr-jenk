package com.buy01.product_service.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Centralized JSON Response Builder
    private Map<String, Object> body(HttpStatus status, String message, HttpServletRequest req) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", status.value());
        map.put("error", status.getReasonPhrase());
        map.put("message", message);
        map.put("path", req.getRequestURI());
        return map;
    }

    // ✅ Handled: DTO Validation Errors (@NotBlank, @Min, @NotNull, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Validation failed");

        log.warn("Validation error at {}: {}", req.getRequestURI(), msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                body(HttpStatus.BAD_REQUEST, msg, req));
    }

    // ✅ Handled: Bad HTTP requests (missing params, unreadable JSON, Enum/Number
    // mismatches)
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<?> handleBadRequest(Exception ex, HttpServletRequest req) {
        String msg = "Malformed request syntax or invalid parameters.";

        if (ex instanceof HttpMessageNotReadableException readableEx) {
            Throwable cause = readableEx.getMostSpecificCause();

            // 1. Check if it's an exact formatting mismatch (like a String into an Enum or
            // Number)
            if (cause instanceof InvalidFormatException formatEx) {
                String fieldName = formatEx.getPath().isEmpty() ? "unknown" : formatEx.getPath().get(0).getFieldName();

                if (formatEx.getTargetType() != null && formatEx.getTargetType().isEnum()) {
                    String acceptedValues = Arrays.toString(formatEx.getTargetType().getEnumConstants());
                    msg = String.format("Invalid value provided for field '%s'. Accepted values are: %s", fieldName,
                            acceptedValues);
                } else if (formatEx.getTargetType() != null
                        && Number.class.isAssignableFrom(formatEx.getTargetType())) {
                    msg = String.format("Invalid value provided for field '%s'. Expected a valid number.", fieldName);
                } else {
                    msg = String.format("Invalid data type provided for field '%s'.", fieldName);
                }
            }
            // 2. Catch generic Jackson mapping errors (like NumberFormatException wrappers)
            else if (cause instanceof com.fasterxml.jackson.databind.JsonMappingException mappingEx) {
                String fieldName = mappingEx.getPath().isEmpty() ? "unknown"
                        : mappingEx.getPath().get(0).getFieldName();
                msg = String.format("Invalid data format for field '%s'. Please check the value type.", fieldName);
            }
            // 3. Absolute fallback
            else {
                msg = "Unreadable JSON payload. Please check your data formatting.";
            }
        } else if (ex instanceof MissingServletRequestParameterException paramEx) {
            msg = "Missing required parameter: " + paramEx.getParameterName();
        }

        log.warn("Bad request at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                body(HttpStatus.BAD_REQUEST, msg, req));
    }

    // ✅ Handled: Item Not Found (For when we build the Product GET endpoints)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("Illegal argument at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                body(HttpStatus.NOT_FOUND, ex.getMessage(), req));
    }

    // ✅ Handled: 404 Not Found for bad URLs (e.g., adding a trailing slash or
    // hitting a fake path)
    @ExceptionHandler({
            NoResourceFoundException.class,
            NoHandlerFoundException.class
    })
    public ResponseEntity<?> handleEndpointNotFound(Exception ex, HttpServletRequest req) {
        log.warn("Endpoint not found at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                body(HttpStatus.NOT_FOUND, "The requested endpoint or resource does not exist.", req));
    }

    // ✅ Handled: Database constraint violations (duplicate keys, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Data integrity violation at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                body(HttpStatus.CONFLICT, "A database conflict occurred. This record might already exist.", req));
    }

    // ✅ Handled: Wrong HTTP methods (Sending GET instead of POST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                body(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req));
    }

    // ✅ Handled: Forbidden (Token is missing, expired, or user is not a SELLER)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        log.warn("Access denied at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                body(HttpStatus.FORBIDDEN, "Access Denied. You do not have permission to perform this action.", req));
    }

    // ✅ Handled: Manual status exceptions (ResponseStatusException)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status).body(
                body(status, ex.getReason() != null ? ex.getReason() : status.getReasonPhrase(), req));
    }

    // ✅ Fallback: Catch all unhandled exceptions to prevent naked 500 HTML pages
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled error at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                body(HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected server error occurred. Our team has been notified.", req));
    }
}