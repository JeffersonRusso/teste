package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("ERROR_HANDLER")
public class ErrorHandlerInterceptor extends TypedTaskInterceptor<ErrorHandlerConfig> {

    public ErrorHandlerInterceptor() {
        super(ErrorHandlerConfig.class);
    }

    @Override
    protected Object interceptTyped(ExecutionContext context, TaskChain next, ErrorHandlerConfig config, TaskDefinition taskDef) {
        try {
            return next.proceed(context);
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted()) {
                throw e;
            }

            if (shouldIgnore(e, config)) {
                return handleIgnoredError(context, taskDef, e, config);
            }
            throw e;
        }
    }

    private Object handleIgnoredError(ExecutionContext context, TaskDefinition taskDef, Exception e, ErrorHandlerConfig config) {
        String nodeId = taskDef.getNodeId().value();
        log.warn(STR."   [ErrorHandler] Error ignored on task \{nodeId}: \{e.getMessage()}");

        // Correção: Acesso via constantes da ExecutionContext
        context.setMeta(nodeId, ExecutionContext.STATUS, 500);
        context.setMeta(nodeId, ExecutionContext.ERROR, STR."ERROR_IGNORED: \{e.getMessage()}");
        context.track(nodeId, "error_handler.applied", true);

        return config.fallbackValue();
    }

    private boolean shouldIgnore(Exception e, ErrorHandlerConfig config) {
        if (config == null || "FAIL".equalsIgnoreCase(config.action())) return false;
        if (config.ignoreExceptions().contains("*") || config.ignoreNodes().contains("*")) return true;
        if (e instanceof PipelineException pe && pe.getNodeId() != null && config.ignoreNodes().contains(pe.getNodeId())) return true;
        if (config.ignoreExceptions().isEmpty() && config.ignoreNodes().isEmpty()) return true;
        return isExceptionInList(e, config);
    }

    private boolean isExceptionInList(Exception e, ErrorHandlerConfig config) {
        String exName = e.getClass().getName();
        String causeName = (e.getCause() != null) ? e.getCause().getClass().getName() : "";
        return config.ignoreExceptions().contains(exName) || config.ignoreExceptions().contains(causeName);
    }
}
