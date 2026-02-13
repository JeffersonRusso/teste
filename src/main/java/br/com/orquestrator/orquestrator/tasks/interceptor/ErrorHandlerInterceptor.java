package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
            if (shouldIgnore(e, config)) {
                log.warn("   [ErrorHandler] Erro ignorado na task {}: {}", taskDef.getNodeId().value(), e.getMessage());

                applyFallback(data, config, taskDef);

                data.addMetadata(TaskMetadataHelper.STATUS, 500);
                data.addMetadata(TaskMetadataHelper.ERROR, "ERROR_IGNORED: " + e.getMessage());
                return;
            }
            throw e;
        }
    }

    private boolean shouldIgnore(Exception e, ErrorHandlerConfig config) {
        if (config == null) return false;
        if ("FAIL".equalsIgnoreCase(config.action())) return false;

        if (config.ignoreExceptions().contains("*") || config.ignoreNodes().contains("*")) return true;

        if (e instanceof PipelineException pe && pe.getNodeId() != null) {
            if (config.ignoreNodes().contains(pe.getNodeId())) return true;
        }

        if (config.ignoreExceptions().isEmpty() && config.ignoreNodes().isEmpty()) return true;

        String exName = e.getClass().getName();
        String causeName = (e.getCause() != null) ? e.getCause().getClass().getName() : "";

        return config.ignoreExceptions().contains(exName) || config.ignoreExceptions().contains(causeName);
    }

    private void applyFallback(TaskData data, ErrorHandlerConfig config, TaskDefinition taskDef) {
        JsonNode fallbackValue = config.fallbackValue();
        List<DataSpec> produces = taskDef.getProduces();

        if (produces != null && fallbackValue != null) {
            for (int i = 0; i < produces.size(); i++) {
                data.put(produces.get(i).name(), fallbackValue);
            }
        }
    }
}
