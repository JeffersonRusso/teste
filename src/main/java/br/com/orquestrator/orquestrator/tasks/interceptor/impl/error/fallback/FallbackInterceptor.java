package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error.fallback;

import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
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
    public TaskResult apply(TaskChain next) {
        try {
            TaskResult result = next.proceed();
            if (!result.isSuccess()) {
                return TaskResult.success(config.value());
            }
            return result;
        } catch (Exception e) {
            log.warn("Fallback ativado para o nó [{}]: {}", nodeId, e.getMessage());
            return TaskResult.success(config.value());
        }
    }
}
