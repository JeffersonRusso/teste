package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ContextSchema;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * ErrorPolicyDecorator: Aplica a política de fail-fast ou tolerância a falhas.
 * Isola a lógica de tratamento de exceções do motor core.
 */
@Slf4j
@RequiredArgsConstructor
public class ErrorPolicyDecorator implements TaskDecorator {

    private final String nodeId;
    private final boolean failFast;

    @Override
    public TaskResult apply(TaskChain next) {
        try {
            return next.proceed();
        } catch (Exception e) {
            return handleFailure(e);
        }
    }

    private TaskResult handleFailure(Exception e) {
        log.error("!!! Falha no nó [{}]: {}", nodeId, e.getMessage());
        
        // Registra o erro no banco de contexto usando o SCHEMA
        ContextHolder.writer().put(ContextSchema.toNodeErrorPath(nodeId), e.getMessage());
        ContextHolder.writer().put(ContextSchema.toNodeStatusPath(nodeId), 500);

        if (failFast) {
            log.debug("Nó [{}] configurado como Fail-Fast. Propagando erro...", nodeId);
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }

        log.warn("Nó [{}] configurado como resiliente. Pipeline continuará sem este resultado.", nodeId);
        return TaskResult.failure(Map.of("error", e.getMessage()));
    }
}
