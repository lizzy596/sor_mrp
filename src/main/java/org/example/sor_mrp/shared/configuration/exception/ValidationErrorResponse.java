package org.example.sor_mrp.shared.configuration.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors,
        LocalDateTime timestamp
) {}