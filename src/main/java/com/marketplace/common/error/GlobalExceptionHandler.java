package com.marketplace.common.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage(),
                req.getRequestURI(), List.of());
    }

    @ExceptionHandler(ForbiddenException.class)
    ResponseEntity<ErrorResponse> forbidden(ForbiddenException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, ex.getMessage(),
                req.getRequestURI(), List.of());
    }

    @ExceptionHandler(InsufficientStockException.class)
    ResponseEntity<ErrorResponse> insufficientStock(InsufficientStockException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ErrorCode.INSUFFICIENT_STOCK, ex.getMessage(),
                req.getRequestURI(), List.of());
    }

    @ExceptionHandler(IllegalStateTransitionException.class)
    ResponseEntity<ErrorResponse> illegalStateTransition(IllegalStateTransitionException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ErrorCode.ILLEGAL_STATE_TRANSITION, ex.getMessage(),
                req.getRequestURI(), List.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponse> constraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        List<ErrorField> fields = ex.getConstraintViolations().stream()
                .map(v -> new ErrorField(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED, "Validation failed",
                req.getRequestURI(), fields);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unexpected(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error on {} {}", req.getMethod(), req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred", req.getRequestURI(), List.of());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        List<ErrorField> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ErrorField(f.getField(), f.getDefaultMessage()))
                .toList();
        var body = new ErrorResponse(Instant.now() ,HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED,
                "Validation failed", ((ServletWebRequest) request).getRequest().getRequestURI(), fields);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex, Object body, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode, @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        var errorBody = new ErrorResponse(Instant.now(), status, codeFor(status),
                status.getReasonPhrase(), ((ServletWebRequest) request).getRequest().getRequestURI(), List.of());

        return ResponseEntity.status(status).body(errorBody);
    }

    private ErrorCode codeFor(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> ErrorCode.MALFORMED_REQUEST;
            case NOT_FOUND -> ErrorCode.RESOURCE_NOT_FOUND;
            case METHOD_NOT_ALLOWED -> ErrorCode.METHOD_NOT_ALLOWED;
            case UNSUPPORTED_MEDIA_TYPE -> ErrorCode.UNSUPPORTED_MEDIA_TYPE;
            default -> status.is5xxServerError() ? ErrorCode.INTERNAL_ERROR : ErrorCode.CONFLICT;
        };
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, ErrorCode errorCode, String message,
                                         String path, List<ErrorField> fields) {
        return new ResponseEntity<>(
                new ErrorResponse(Instant.now(), status, errorCode, message, path, fields), status);
    }
}
