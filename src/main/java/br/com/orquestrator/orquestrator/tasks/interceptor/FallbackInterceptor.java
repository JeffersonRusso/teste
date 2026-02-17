package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.FallbackConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Interceptor responsável por aplicar valores de fallback em caso de falha na execução da task.
 */
@Slf4j
@Component("FALLBACK")
public class FallbackInterceptor extends TypedTaskInterceptor<FallbackConfig> {

    public FallbackInterceptor() {
        super(FallbackConfig.class);
    }

    @Override
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, FallbackConfig config, TaskDefinition taskDef) {
        try {
            return next.proceed(context);
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted()) {
                throw (RuntimeException) e;
            }
            return handleFallback(context, config, taskDef, e);
        }
    }

    private TaskResult handleFallback(ExecutionContext context, FallbackConfig config, TaskDefinition taskDef, Exception e) {
        String nodeId = taskDef.getNodeId().value();
        log.warn(STR."Task '\{nodeId}' failed. Applying fallback logic. Reason: \{e.getMessage()}");

        context.track(nodeId, "fallback.applied", true);
        context.track(nodeId, "fallback.error_type", e.getClass().getSimpleName());

        return new TaskResult(config.value(), 200, Map.of("fallback_applied", true));
    }
}
