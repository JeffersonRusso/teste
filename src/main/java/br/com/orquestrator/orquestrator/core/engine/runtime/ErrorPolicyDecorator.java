package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ContextSchema;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ErrorPolicyDecorator implements TaskDecorator {

    private final String nodeId;
    private final boolean failFast;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        try {
            return next.proceed(context);
        } catch (Exception e) {
            return handleFailure(e);
        }
    }

    private TaskResult handleFailure(Exception e) {
        log.error("!!! Falha no nó [{}]: {}", nodeId, e.getMessage());
        
        // Corrigido: Usando DataValue.of
        ContextHolder.writer().put(ContextSchema.toNodeErrorPath(nodeId), DataValue.of(e.getMessage()));
        ContextHolder.writer().put(ContextSchema.toNodeStatusPath(nodeId), DataValue.of(500));

        if (failFast) {
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }

        return TaskResult.failure(Map.of("error", e.getMessage()));
    }
}
