package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.StructuredTaskScope;

/**
 * ReactiveExecutionEngine: Motor de execução puro.
 * Não contém lógica de negócio ou política de erro; apenas orquestra threads e sinais.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveExecutionEngine {

    public void execute(Pipeline pipeline) {
        Instant deadline = ExecutionClock.calculateDeadline(pipeline.timeout());

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            
            pipeline.getNodes().forEach(node -> scope.fork(() -> {
                executeNode(node);
                return null;
            }));

            scope.joinUntil(deadline);
            scope.throwIfFailed();
            
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private void executeNode(Pipeline.TaskNode node) {
        try {
            node.dependencies().forEach(java.util.concurrent.CompletableFuture::join);
            node.executable().execute();
        } finally {
            node.signalsToEmit().forEach(s -> s.complete(null));
        }
    }

    private RuntimeException handleException(Exception e) {
        if (e instanceof PipelineException pe) return pe;
        if (e instanceof java.util.concurrent.TimeoutException) {
            return new PipelineException("Timeout atingido durante a execução do pipeline");
        }
        log.error("Falha crítica no motor reativo: {}", e.getMessage());
        return new PipelineException("Erro interno na execução do pipeline", e);
    }
}
