package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.circuitbreak;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CircuitBreakerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CircuitBreakerInterceptorProvider implements InterceptorProvider<CircuitBreakerConfig> {

    private final CircuitBreakerInterceptor interceptor;

    @Override
    public String featureType() {
        return "CIRCUIT_BREAKER";
    }

    @Override
    public Class<CircuitBreakerConfig> configClass() {
        return CircuitBreakerConfig.class;
    }

    @Override
    public TaskInterceptor create(CircuitBreakerConfig config, String nodeId) {
        return interceptor.adapt(config, nodeId);
    }
}
