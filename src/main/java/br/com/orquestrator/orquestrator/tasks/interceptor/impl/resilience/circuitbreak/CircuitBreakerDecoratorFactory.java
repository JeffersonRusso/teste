package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.circuitbreak;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CircuitBreakerDecoratorFactory implements DecoratorFactory<CircuitBreakerConfig> {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override public String getType() { return "CIRCUIT_BREAKER"; }
    @Override public Class<CircuitBreakerConfig> getConfigClass() { return CircuitBreakerConfig.class; }

    @Override
    public TaskInterceptor create(CircuitBreakerConfig config, String nodeId) {
        // Corrigido: Usando os métodos getter que fazem o unboxing e conversão correta
        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig cbConfig = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(config.getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofMillis(config.getWaitDurationMs()))
                .permittedNumberOfCallsInHalfOpenState(config.getPermittedCalls())
                .slidingWindowSize(config.getSlidingWindowSize())
                .build();

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(nodeId, cbConfig);
        return new CircuitBreakerInterceptor(cb);
    }
}
