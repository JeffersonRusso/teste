package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.tasks.interceptor.config.CircuitBreakerConfig;
import br.com.orquestrator.orquestrator.tasks.interceptor.resilience.CircuitBreakerStrategy;
import br.com.orquestrator.orquestrator.tasks.interceptor.resilience.ResilienceInterceptor;
import org.springframework.stereotype.Component;

/**
 * CircuitBreakerInterceptor: Agora apenas uma casca para a CircuitBreakerStrategy.
 */
@Component("CIRCUIT_BREAKER")
public class CircuitBreakerInterceptor extends ResilienceInterceptor<CircuitBreakerConfig> {
    public CircuitBreakerInterceptor(CircuitBreakerStrategy strategy) {
        super(CircuitBreakerConfig.class, strategy);
    }
}
