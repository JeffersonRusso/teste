package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.circuitbreak;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CircuitBreakerDecoratorFactory implements DecoratorFactory<CircuitBreakerConfig> {

    @Override
    public String getType() {
        return "CIRCUIT_BREAKER";
    }

    @Override
    public Class<CircuitBreakerConfig> getConfigClass() {
        return CircuitBreakerConfig.class;
    }

    @Override
    public TaskDecorator create(CircuitBreakerConfig config, String nodeId) {
        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig r4jConfig = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(config.getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofMillis(config.getWaitDurationMs()))
                .slidingWindowSize(config.getSlidingWindowSize())
                .permittedNumberOfCallsInHalfOpenState(config.getPermittedCalls())
                .build();

        return new CircuitBreakerInterceptor(CircuitBreaker.of(nodeId, r4jConfig));
    }
}
