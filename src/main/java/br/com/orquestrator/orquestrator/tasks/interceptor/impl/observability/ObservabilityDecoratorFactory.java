package br.com.orquestrator.orquestrator.tasks.interceptor.impl.observability;

import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventPublisher;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ObservabilityDecoratorFactory implements DecoratorFactory<Map<String, Object>> {

    private final PipelineEventPublisher eventPublisher;

    @Override
    public String getType() {
        return "OBSERVABILITY";
    }

    @Override
    public Class<Map<String, Object>> getConfigClass() {
        return (Class) Map.class;
    }

    @Override
    public TaskDecorator create(Map<String, Object> config, String nodeId) {
        return new ObservabilityInterceptor(eventPublisher, nodeId);
    }
}
