package br.com.orquestrator.orquestrator.tasks.interceptor.impl.observability;

import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEvent;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
@RequiredArgsConstructor
public final class ObservabilityInterceptor implements TaskInterceptor {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public TaskResult intercept(TaskExecutionContext context, TaskChain chain) {
        String nodeId = context.getTaskName();
        long start = System.currentTimeMillis();

        try {
            eventPublisher.publishEvent(new PipelineEvent.TaskStarted(nodeId));
            
            TaskResult result = chain.proceed(context);
            
            long duration = System.currentTimeMillis() - start;
            if (result instanceof TaskResult.Success s) {
                eventPublisher.publishEvent(new PipelineEvent.TaskFinished(nodeId, s.body().asNative(), duration));
            } else {
                eventPublisher.publishEvent(new PipelineEvent.TaskFinished(nodeId, null, duration));
            }
            
            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            eventPublisher.publishEvent(new PipelineEvent.TaskFailed(nodeId, e, duration));
            throw e;
        }
    }
}
