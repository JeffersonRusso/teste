package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ContextSchema;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TelemetryDecorator: Responsável por medir a performance e latência de cada nó.
 * Registra os dados no banco de contexto para análise posterior.
 */
@Slf4j
@RequiredArgsConstructor
public class TelemetryDecorator implements TaskDecorator {

    private final String nodeId;

    @Override
    public TaskResult apply(TaskChain next) {
        long start = System.nanoTime();
        try {
            TaskResult result = next.proceed();
            recordMetrics(start, true);
            return result;
        } catch (Exception e) {
            recordMetrics(start, false);
            throw e;
        }
    }

    private void recordMetrics(long startNano, boolean success) {
        long durationMs = (System.nanoTime() - startNano) / 1_000_000;
        
        // Registra a latência no banco de contexto (visão de escrita)
        String path = ContextSchema.toNodeResultPath(nodeId) + ".metrics";
        ContextHolder.writer().put(path + ".durationMs", durationMs);
        ContextHolder.writer().put(path + ".success", success);
        
        log.debug("Nó [{}] finalizado em {}ms | Sucesso: {}", nodeId, durationMs, success);
    }
}
