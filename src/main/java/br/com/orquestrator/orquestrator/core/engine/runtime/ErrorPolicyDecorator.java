package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * ErrorPolicyDecorator: Aplica a política de erro (fail-fast ou fail-safe).
 * Agora desacoplado do ContextHolder e focado no fluxo de sinais (Dataflow).
 */
@Slf4j
@RequiredArgsConstructor
public class ErrorPolicyDecorator implements TaskInterceptor {

    private final String nodeId;
    private final boolean failFast;

    @Override
    public TaskResult intercept(Chain chain) {
        try {
            return chain.proceed(chain.context());
        } catch (Exception e) {
            return handleFailure(e);
        }
    }

    private TaskResult handleFailure(Exception e) {
        log.error("!!! Falha no nó [{}]: {}", nodeId, e.getMessage());
        
        // No novo modelo, não gravamos mais no ContextHolder.
        // O erro flui via TaskResult e é tratado pelo DefaultExecutionNode.
        
        if (failFast) {
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }

        // Se não for fail-fast, retornamos um resultado de falha que o motor saberá lidar
        return TaskResult.failure(Map.of("error", e.getMessage()));
    }
}
