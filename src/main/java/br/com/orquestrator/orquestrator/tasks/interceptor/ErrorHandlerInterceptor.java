package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import br.com.orquestrator.orquestrator.tasks.interceptor.error.ErrorIgnoreStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component("ERROR_HANDLER")
public class ErrorHandlerInterceptor extends TypedTaskInterceptor<ErrorHandlerConfig> {

    private final List<ErrorIgnoreStrategy> ignoreStrategies;

    public ErrorHandlerInterceptor(List<ErrorIgnoreStrategy> ignoreStrategies) {
        super(ErrorHandlerConfig.class);
        this.ignoreStrategies = ignoreStrategies;
    }

    @Override
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, ErrorHandlerConfig config, TaskDefinition taskDef) {
        try {
            return next.proceed(context);
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted()) throw e;

            if (shouldIgnore(e, config)) {
                return handleIgnoredError(context, taskDef, e, config);
            }
            
            context.setError(taskDef.getNodeId().value(), e.getMessage());
            throw e;
        }
    }

    private boolean shouldIgnore(Exception e, ErrorHandlerConfig config) {
        if (config == null || "FAIL".equalsIgnoreCase(config.action())) return false;
        
        // Se não houver regras específicas, o padrão é ignorar (conforme lógica original)
        if (config.ignoreExceptions().isEmpty() && config.ignoreNodes().isEmpty()) return true;

        return ignoreStrategies.stream().anyMatch(s -> s.shouldIgnore(e, config));
    }

    private TaskResult handleIgnoredError(ExecutionContext context, TaskDefinition taskDef, Exception e, ErrorHandlerConfig config) {
        String nodeId = taskDef.getNodeId().value();
        log.warn(STR."   [ErrorHandler] Error ignored on task \{nodeId}: \{e.getMessage()}");

        context.setStatus(nodeId, 500);
        context.setError(nodeId, STR."ERROR_IGNORED: \{e.getMessage()}");
        context.track(nodeId, "error_handler.applied", true);

        return new TaskResult(config.fallbackValue(), 500, Map.of("error_ignored", true));
    }
}
