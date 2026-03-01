package br.com.orquestrator.orquestrator.tasks.interceptor.impl.logging;

import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
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
    public TaskResult apply(TaskChain next) {
        TaskResult result = next.proceed();
        if (config != null && !"OFF".equalsIgnoreCase(config.getLevel())) {
            executeLogging(result);
        }
        return result;
    }

    private void executeLogging(TaskResult result) {
        String level = config.getLevel().toUpperCase();
        switch (level) {
            case "DEBUG" -> {
                if (log.isDebugEnabled()) {
                    log.debug("Task '{}' finalizada com status: {}", nodeId, result.status());
                    if (config.isShowBody() && result.body() != null) {
                        log.debug("   Corpo: {}", result.body());
                    }
                }
            }
            case "WARN" -> log.warn("Task '{}' finalizada com status: {}", nodeId, result.status());
            case "ERROR" -> log.error("Task '{}' finalizada com status: {}", nodeId, result.status());
            default -> {
                if (log.isInfoEnabled()) {
                    log.info("Task '{}' finalizada com status: {}", nodeId, result.status());
                }
            }
        }
    }
}
