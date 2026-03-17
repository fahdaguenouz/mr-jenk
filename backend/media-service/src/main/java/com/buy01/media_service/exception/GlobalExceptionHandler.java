package com.buy01.media_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Centralized JSON Response Builder (Matches your other services)
    private Map<String, Object> body(HttpStatus status, String message, HttpServletRequest req) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", status.value());
        map.put("error", status.getReasonPhrase());
        map.put("message", message);
        map.put("path", req.getRequestURI());
        return map;
    }

    // ✅ Handled: Files that exceed the 2MB limit
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest req) {
        log.warn("Max upload size exceeded at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                body(HttpStatus.BAD_REQUEST, "File is too large. Maximum allowed size is 2 MB.", req));
    }

    // ✅ Handled: Invalid file types (thrown from controller/service)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("Illegal argument at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                body(HttpStatus.BAD_REQUEST, ex.getMessage(), req));
    }

    // ✅ Handled: Forbidden (Token missing, expired, or user is not a SELLER)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        log.warn("Access denied at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                body(HttpStatus.FORBIDDEN, "Access Denied. You do not have permission to perform this action.", req));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest req) {
        log.warn("Resource not found at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                body(HttpStatus.NOT_FOUND, "The requested endpoint or resource was not found.", req));
    }

    // Add this alongside your other handlers
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingFile(MissingServletRequestPartException ex, HttpServletRequest req) {
        log.warn("Missing file part at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                body(HttpStatus.BAD_REQUEST,
                        "A file is required. Please ensure you are sending a multipart request with the key 'file'.",
                        req));
    }

    // ✅ Handled: Request is not a multipart/form-data request
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<?> handleMultipartException(MultipartException ex, HttpServletRequest req) {
        log.warn("Multipart request error at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                body(HttpStatus.BAD_REQUEST,
                        "The request must be a file upload. Please ensure your client is sending a 'multipart/form-data' request.",
                        req));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req) {
        log.warn("Method not supported at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                body(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not allowed for this endpoint. " + ex.getMessage(),
                        req));
    }

    // ✅ Fallback: Catch-all for any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex, HttpServletRequest req) {
        log.error("Unhandled error at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                body(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred.", req));
    }
}