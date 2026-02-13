package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
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
    protected void interceptTyped(TaskData data, TaskChain next, ErrorHandlerConfig config, TaskDefinition taskDef) {
        try {
            next.proceed(data);
        } catch (Exception e) {
            // Java 21: Proteção contra interrupções de sistema
            if (Thread.currentThread().isInterrupted()) {
                throw e;
            }

            if (shouldIgnore(e, config)) {
                handleIgnoredError(data, taskDef, e, config);
                return;
            }
            throw e;
        }
    }

    private void handleIgnoredError(TaskData data, TaskDefinition taskDef, Exception e, ErrorHandlerConfig config) {
        String nodeId = taskDef.getNodeId().value();
        log.warn(STR."   [ErrorHandler] Error ignored on task \{nodeId}: \{e.getMessage()}");

        // Centralização do rastro no ExecutionContext
        data.addMetadata(TaskMetadataHelper.STATUS, 500);
        data.addMetadata(TaskMetadataHelper.ERROR, STR."ERROR_IGNORED: \{e.getMessage()}");
        data.addMetadata("error_handler.applied", true);

        // Se houver valor de fallback configurado, aplicamos
        if (config.fallbackValue() != null) {
            applySimpleFallback(data, taskDef, config.fallbackValue());
        }
    }

    private boolean shouldIgnore(Exception e, ErrorHandlerConfig config) {
        if (config == null || "FAIL".equalsIgnoreCase(config.action())) return false;

        // Regra de wildcard: ignora tudo
        if (config.ignoreExceptions().contains("*") || config.ignoreNodes().contains("*")) return true;

        // Verificação por ID do Nó (específico para PipelineException)
        if (e instanceof PipelineException pe && pe.getNodeId() != null
                && config.ignoreNodes().contains(pe.getNodeId())) {
            return true;
        }

        // Se as listas estiverem vazias e a ação não for FAIL, assume-se que deve ignorar
        if (config.ignoreExceptions().isEmpty() && config.ignoreNodes().isEmpty()) return true;

        // Verificação por tipo de exceção (Hierarquia simples)
        return isExceptionInList(e, config);
    }

    private boolean isExceptionInList(Exception e, ErrorHandlerConfig config) {
        String exName = e.getClass().getName();
        String causeName = (e.getCause() != null) ? e.getCause().getClass().getName() : "";
        return config.ignoreExceptions().contains(exName) || config.ignoreExceptions().contains(causeName);
    }

    private void applySimpleFallback(TaskData data, TaskDefinition taskDef, Object value) {
        // Lógica simplificada: se a task produz dados, preenchemos todos com o valor de escape
        if (taskDef.getProduces() != null) {
            taskDef.getProduces().forEach(spec -> data.put(spec.name(), value));
        }
    }
}