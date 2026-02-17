package br.com.orquestrator.orquestrator.tasks.interceptor.resilience;

import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class CircuitBreakerStrategy implements ResilienceStrategy<CircuitBreakerConfig> {

    private final CircuitBreakerRegistry registry;

    @Override
    public TaskResult execute(Supplier<TaskResult> execution, String resourceId, CircuitBreakerConfig config) {
        CircuitBreaker cb = registry.circuitBreaker(
                STR."\{resourceId}_\{config.hashCode()}",
                () -> io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                        .failureRateThreshold(config.getFailureRateThreshold())
                        .waitDurationInOpenState(Duration.ofMillis(config.getWaitDurationMs()))
                        .permittedNumberOfCallsInHalfOpenState(config.getPermittedCalls())
                        .slidingWindowSize(config.getSlidingWindowSize())
                        .ignoreExceptions(InterruptedException.class)
                        .build()
        );

        try {
            return cb.executeSupplier(execution);
        } catch (CallNotPermittedException e) {
            log.warn(STR."   [CircuitBreaker] Short-circuit ativado para: \{resourceId}");
            throw new PipelineException(STR."Circuit Breaker \{cb.getState()} para \{resourceId}")
                    .withNodeId(resourceId)
                    .addMetadata("state", cb.getState().name());
        }
    }

    @Override
    public String getType() { return "CIRCUIT_BREAKER"; }
}
