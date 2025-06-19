package dev.svistunov.springdemo.dto.response;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public record ErrorResponse(String message, Map<String, String> errors, Instant timestamp) {
    public ErrorResponse(String message, Map<String, String> errors) {
        this(message, errors, Instant.now());
    }

    public ErrorResponse(String message) {
        this(message, Collections.emptyMap(), Instant.now());
    }
}
