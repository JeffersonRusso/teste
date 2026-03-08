package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.domain.model.DataValueNavigator;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;

/**
 * ReactiveExecutionEngine: O motor de execução baseado em Dataflow.
 * Otimizado para alta volumetria e diagnóstico de falhas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveExecutionEngine {

    public Map<String, Object> execute(Pipeline pipeline, RequestIdentity identity, Map<String, Object> rawInput) {
        Instant deadline = ExecutionClock.calculateDeadline(pipeline.timeout());
        SignalRegistry signals = new SignalRegistry();

        // 1. Injeta o input inicial
        signals.emit("raw", DataValueFactory.of(rawInput));

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            // 2. Dispara todos os nós em Virtual Threads
            pipeline.getNodes().forEach(node -> scope.fork(() -> {
                node.run(signals);
                return null;
            }));

            try {
                // 3. Aguarda conclusão respeitando o deadline do pipeline
                scope.joinUntil(deadline);
                scope.throwIfFailed();
            } catch (TimeoutException e) {
                log.error("!!! PIPELINE TIMEOUT [{}]: A execução excedeu o limite de {}ms", 
                        identity.correlationId(), pipeline.timeout().toMillis());
                throw new PipelineException("Timeout atingido durante a execução do pipeline");
            } catch (Exception e) {
                log.error("!!! PIPELINE FAILURE [{}]: Um dos nós falhou e interrompeu o fluxo. Causa: {}", 
                        identity.correlationId(), e.getMessage());
                throw e;
            }

            // 4. Extração Soberana do Resultado
            return collectFinalResults(pipeline, signals);

        } catch (Exception e) {
            throw handleException(e, identity);
        }
    }

    private Map<String, Object> collectFinalResults(Pipeline pipeline, SignalRegistry signals) {
        Map<String, Object> results = new HashMap<>();
        if (pipeline.requiredOutputs() != null) {
            pipeline.requiredOutputs().forEach(path -> {
                try {
                    DataValue signalValue = signals.await(path.getRoot());
                    // ALOCAÇÃO ZERO: Usa o objeto subPath já pronto
                    DataValue extracted = DataValueNavigator.navigate(signalValue, path.getSubPathObject());
                    if (!extracted.isEmpty()) {
                        results.put(path.value(), extracted.raw());
                    }
                } catch (Exception e) {
                    log.warn("Sinal obrigatório não satisfeito: {}", path.value());
                }
            });
        }
        return results;
    }

    private RuntimeException handleException(Exception e, RequestIdentity identity) {
        PipelineException pe;
        if (e instanceof PipelineException p) {
            pe = p;
        } else {
            pe = new PipelineException("Erro interno na execução do pipeline", e);
        }
        return pe.withIdentity(identity.correlationId(), identity.operationType());
    }
}
