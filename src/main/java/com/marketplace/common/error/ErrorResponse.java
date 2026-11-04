package com.marketplace.common.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    HttpStatus status;
    ErrorCode error;
    String message;
    String path;
    List<ErrorField> fieldErrors;
}