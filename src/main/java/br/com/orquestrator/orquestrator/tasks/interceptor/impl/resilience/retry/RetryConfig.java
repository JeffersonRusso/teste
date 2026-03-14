package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.retry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RetryConfig(
    int maxAttempts,
    long waitDurationMs,
    boolean exponentialBackoff
) {
    public RetryConfig {
        if (maxAttempts <= 0) maxAttempts = 3;
        if (waitDurationMs <= 0) waitDurationMs = 500;
    }
}
