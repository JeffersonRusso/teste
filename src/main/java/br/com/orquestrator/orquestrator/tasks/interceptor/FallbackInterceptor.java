package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.FallbackConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Interceptor responsável por aplicar valores de fallback em caso de falha na execução da task.
 */
@Slf4j
@Component("FALLBACK")
public class FallbackInterceptor extends TypedTaskInterceptor<FallbackConfig> {

    private final ObjectMapper objectMapper;

    public FallbackInterceptor(ObjectMapper objectMapper) {
        super(FallbackConfig.class);
        this.objectMapper = objectMapper;
    }

    @Override
    protected Object interceptTyped(ExecutionContext context, TaskChain next, FallbackConfig config, TaskDefinition taskDef) {
        try {
            return next.proceed(context);
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted()) {
                throw (RuntimeException) e;
            }
            return handleFallback(context, config, taskDef, e);
        }
    }

    private Object handleFallback(ExecutionContext context, FallbackConfig config, TaskDefinition taskDef, Exception e) {
        String nodeId = taskDef.getNodeId().value();
        log.warn(STR."Task '\{nodeId}' failed. Applying fallback logic. Reason: \{e.getMessage()}");

        context.track(nodeId, "fallback.applied", true);
        context.track(nodeId, "fallback.error_type", e.getClass().getSimpleName());

        return config.value() != null ? objectMapper.convertValue(config.value(), Object.class) : null;
    }
}
