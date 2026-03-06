package br.com.orquestrator.orquestrator.tasks.interceptor.impl.observability;

import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventPublisher;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObservabilityInterceptor implements TaskDecorator {

    private final PipelineEventPublisher eventPublisher;
    private final String nodeId;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        long start = System.currentTimeMillis();
        TaskResult result = next.proceed(context);
        long duration = System.currentTimeMillis() - start;

        eventPublisher.publishTaskFinished(nodeId, result.body(), duration);
        return result;
    }
}
