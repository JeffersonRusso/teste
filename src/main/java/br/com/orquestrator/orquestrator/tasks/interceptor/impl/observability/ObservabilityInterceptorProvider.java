package br.com.orquestrator.orquestrator.tasks.interceptor.impl.observability;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservabilityInterceptorProvider implements InterceptorProvider<Object> {

    private final ObservabilityInterceptor interceptor;

    @Override
    public String featureType() {
        return "OBSERVABILITY";
    }

    @Override
    public Class<Object> configClass() {
        return Object.class;
    }

    @Override
    public TaskInterceptor create(Object config, String nodeId) {
        // O interceptor de observabilidade agora recebe o nodeId como String
        return interceptor.create(nodeId);
    }
}
