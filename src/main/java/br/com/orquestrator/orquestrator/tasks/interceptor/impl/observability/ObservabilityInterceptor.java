package br.com.orquestrator.orquestrator.tasks.interceptor.impl.observability;

import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventPublisher;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

/**
 * ObservabilityInterceptor: Captura métricas de execução da tarefa.
 */
@RequiredArgsConstructor
public class ObservabilityInterceptor implements TaskInterceptor {

    private final PipelineEventPublisher eventPublisher;
    private final String nodeId;

    @Override
    public TaskResult intercept(Chain chain) {
        long start = System.currentTimeMillis();
        TaskResult result = chain.proceed(chain.inputs());
        long duration = System.currentTimeMillis() - start;

        eventPublisher.publishTaskFinished(nodeId, result.body(), duration);
        return result;
    }
}
