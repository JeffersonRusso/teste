package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.circuitbreak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CircuitBreakerConfig(
    Double failureRateThreshold,
    Long waitDurationInOpenStateMs,
    Integer permittedNumberOfCallsInHalfOpenState,
    Integer slidingWindowSize
) {

}
