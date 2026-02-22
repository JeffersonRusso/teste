package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.FallbackConfig;
import br.com.orquestrator.orquestrator.tasks.interceptor.core.TypedTaskInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("FALLBACK_INTERCEPTOR")
public class FallbackInterceptor extends TypedTaskInterceptor<FallbackConfig> {

    public FallbackInterceptor() {
        super(FallbackConfig.class);
    }

    @Override
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, FallbackConfig config, String nodeId) {
        try {
            return next.proceed(context);
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted()) {
                throw (RuntimeException) e;
            }
            return handleFallback(context, nodeId, config, e);
        }
    }

    private TaskResult handleFallback(ExecutionContext context, String nodeId, FallbackConfig config, Exception e) {
        log.warn("Task '{}' failed. Applying fallback logic. Reason: {}", nodeId, e.getMessage());

        context.track(nodeId, "fallback.applied", true);
        context.track(nodeId, "fallback.error_type", e.getClass().getSimpleName());

        return new TaskResult(config.value(), 200, Map.of("fallback_applied", true));
    }
}
