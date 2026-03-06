package br.com.orquestrator.orquestrator.tasks.interceptor.impl.logging;

import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.LogResponseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LogResponseInterceptor implements TaskDecorator {

    private final LogResponseConfig config;
    private final String nodeId;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        TaskResult result = next.proceed(context);
        
        if (log.isInfoEnabled()) {
            log.info("Nó [{}] retornou status: {} | Body: {}", nodeId, result.status(), result.body());
        }
        
        return result;
    }
}
