package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error.fallback;

import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.FallbackConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FallbackInterceptor implements TaskDecorator {

    private final FallbackConfig config;
    private final String nodeId;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        try {
            return next.proceed(context);
        } catch (Exception e) {
            log.warn("Acionando Fallback para o nó [{}]: {}", nodeId, e.getMessage());
            return TaskResult.success(config.value());
        }
    }
}
