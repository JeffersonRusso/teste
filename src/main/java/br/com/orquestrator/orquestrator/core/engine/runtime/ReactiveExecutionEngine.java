package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.StructuredTaskScope;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveExecutionEngine {

    public void execute(Pipeline pipeline) {
        Instant deadline = ExecutionClock.calculateDeadline(pipeline.timeout());
        SignalRegistry signals = new SignalRegistry();

        // OTIMIZAÇÃO: Usamos o escopo base para permitir que falhas individuais
        // sejam tratadas pelos Decorators (Retry, ErrorPolicy) sem derrubar o motor.
        try (var scope = new StructuredTaskScope<Void>()) {

            pipeline.getNodes().forEach(node -> scope.fork(() -> {
                node.run(signals);
                return null;
            }));

            scope.joinUntil(deadline);

        } catch (Exception e) {
            throw handleException(e);
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
