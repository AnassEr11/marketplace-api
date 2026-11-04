package com.marketplace.common.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorField {
    private String field;
    private String message;
}
