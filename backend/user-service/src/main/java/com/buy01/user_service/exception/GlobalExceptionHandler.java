package com.buy01.user_service.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Centralized response builder
    private Map<String, Object> body(HttpStatus status, String message, HttpServletRequest req) {
        Map<String, Object> b = new HashMap<>();
        b.put("timestamp", Instant.now().toString());
        b.put("status", status.value());
        b.put("error", status.getReasonPhrase());
        b.put("message", message);
        b.put("path", req.getRequestURI());
        return b;
    }

    // ✅ Handled: "Email/Username already in use" from AuthService
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("Illegal argument at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                body(HttpStatus.BAD_REQUEST, ex.getMessage(), req));
    }
    // ✅ Handled: Unsupported Content-Type (e.g., sending multipart/form-data when JSON is expected)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        log.warn("Unsupported media type at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                body(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type. Please ensure you are sending a standard JSON request (application/json).", req));
    }
    // ✅ Handled: Wrong password or missing user during login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        log.warn("Failed login attempt at {}", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                body(HttpStatus.UNAUTHORIZED, "Invalid email/username, or password.", req));
    }

    // ✅ Handled: Validation errors from DTOs (e.g., password too short, invalid
    // email)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Validation failed");

        log.warn("Validation failed at {}: {}", req.getRequestURI(), msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                body(HttpStatus.BAD_REQUEST, msg, req));
    }

    // ✅ Handled: Database constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Data integrity violation at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                body(HttpStatus.CONFLICT, "A database conflict occurred. This record might already exist.", req));
    }

    // ✅ Handled: Role-based access restrictions (@PreAuthorize)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                body(HttpStatus.FORBIDDEN, "You do not have permission to access this resource.", req));
    }

    // ✅ Handled: Invalid or expired tokens
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwt(JwtException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                body(HttpStatus.UNAUTHORIZED, "Invalid or expired authentication token. Please log in again.", req));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNotFound(
            NoHandlerFoundException ex,
            HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(
                        HttpStatus.NOT_FOUND,
                        "Endpoint not found.",
                        req));
    }

    // ✅ Handled: Bad HTTP requests (missing params, unreadable JSON)
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<?> handleBadRequest(Exception ex, HttpServletRequest req) {
        log.warn("Malformed request at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                body(HttpStatus.BAD_REQUEST, "Malformed request syntax or invalid parameters.", req));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {

        String message = "Malformed request.";

        if (ex.getMessage().contains("Role")) {
            message = "Invalid role value. Allowed values: CLIENT, ADMIN, SELLER.";
        }

        log.warn("Malformed JSON at {}: {}", req.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body(HttpStatus.BAD_REQUEST, message, req));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(body(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        "HTTP method not allowed for this endpoint.",
                        req));
    }

    // ✅ Handled: Manual status exceptions
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status).body(
                body(status, ex.getReason() != null ? ex.getReason() : status.getReasonPhrase(), req));
    }

    // ✅ Fallback: Catch all unhandled exceptions to prevent naked 500 pages
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled error at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                body(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred, Server error.", req));
    }
}