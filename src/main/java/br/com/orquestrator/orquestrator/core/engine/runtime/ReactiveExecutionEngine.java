package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveExecutionEngine {

    private final ObjectMapper objectMapper;

    public Map<String, Object> execute(Pipeline pipeline, RequestIdentity identity, Map<String, Object> rawInput) {
        Instant deadline = ExecutionClock.calculateDeadline(pipeline.timeout());
        SignalRegistry signals = new SignalRegistry();

        signals.emit("raw", objectMapper.valueToTree(rawInput));

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            pipeline.getNodes().forEach(node -> scope.fork(() -> {
                node.run(signals);
                return null;
            }));

            try {
                scope.joinUntil(deadline);
                scope.throwIfFailed();
            } catch (TimeoutException e) {
                log.error("!!! PIPELINE TIMEOUT [{}]: A execução excedeu o limite de {}ms", 
                        identity.getCorrelationId(), pipeline.timeout().toMillis());
                throw new PipelineException("Timeout atingido durante a execução do pipeline");
            } catch (Exception e) {
                log.error("!!! PIPELINE FAILURE [{}]: Um dos nós falhou e interrompeu o fluxo. Causa: {}", 
                        identity.getCorrelationId(), e.getMessage());
                throw e;
            }

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
                    JsonNode value = signals.get(path);
                    if (!value.isMissingNode()) {
                        results.put(path, value);
                    }
                } catch (Exception e) {
                    log.warn("Sinal obrigatório não satisfeito: {}", path);
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
        return pe.withIdentity(identity.getCorrelationId(), identity.getOperationType());
    }
}
