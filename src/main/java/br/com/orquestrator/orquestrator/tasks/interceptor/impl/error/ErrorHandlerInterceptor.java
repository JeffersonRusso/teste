package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import br.com.orquestrator.orquestrator.tasks.interceptor.core.TypedTaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.error.ErrorIgnoreStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component("ERROR_HANDLER_INTERCEPTOR")
public class ErrorHandlerInterceptor extends TypedTaskInterceptor<ErrorHandlerConfig> {

    private final List<ErrorIgnoreStrategy> ignoreStrategies;

    public ErrorHandlerInterceptor(List<ErrorIgnoreStrategy> ignoreStrategies) {
        super(ErrorHandlerConfig.class);
        this.ignoreStrategies = ignoreStrategies;
    }

    @Override
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, ErrorHandlerConfig config, String nodeId) {
        try {
            return next.proceed(context);
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted()) throw e;

            if (shouldIgnore(e, config)) {
                return handleIgnoredError(context, nodeId, e, config);
            }
            
            context.setError(nodeId, e.getMessage());
            throw e;
        }
    }

    private boolean shouldIgnore(Exception e, ErrorHandlerConfig config) {
        if (config == null || "FAIL".equalsIgnoreCase(config.action())) return false;
        if (config.ignoreExceptions().isEmpty() && config.ignoreNodes().isEmpty()) return true;
        return ignoreStrategies.stream().anyMatch(s -> s.shouldIgnore(e, config));
    }

    private TaskResult handleIgnoredError(ExecutionContext context, String nodeId, Exception e, ErrorHandlerConfig config) {
        log.warn("   [ErrorHandler] Error ignored on task {}: {}", nodeId, e.getMessage());

        context.setStatus(nodeId, 500);
        context.setError(nodeId, "ERROR_IGNORED: " + e.getMessage());
        context.track(nodeId, "error_handler.applied", true);

        return new TaskResult(config.fallbackValue(), 500, Map.of("error_ignored", true));
    }
}
