package br.com.orquestrator.orquestrator.tasks.interceptor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CircuitBreakerConfig(
    Double failureRateThreshold,
    Long waitDurationInOpenStateMs,
    Integer permittedNumberOfCallsInHalfOpenState,
    Integer slidingWindowSize
) {
    public float getFailureRateThreshold() {
        return failureRateThreshold != null ? failureRateThreshold.floatValue() : 50.0f;
    }

    public long getWaitDurationMs() {
        return waitDurationInOpenStateMs != null ? waitDurationInOpenStateMs : 10000L;
    }

    public int getPermittedCalls() {
        return permittedNumberOfCallsInHalfOpenState != null ? permittedNumberOfCallsInHalfOpenState : 5;
    }

    public int getSlidingWindowSize() {
        return slidingWindowSize != null ? slidingWindowSize : 10;
    }
}
