package br.com.orquestrator.orquestrator.core.engine.strategy;

import br.com.orquestrator.orquestrator.core.context.OrquestratorContext;
import br.com.orquestrator.orquestrator.core.engine.runtime.SignalRegistry;
import br.com.orquestrator.orquestrator.core.engine.support.ExecutionClock;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import br.com.orquestrator.orquestrator.api.signal.CoreSignals;
import br.com.orquestrator.orquestrator.api.signal.Signal;
import br.com.orquestrator.orquestrator.api.signal.SignalResult;
import br.com.orquestrator.orquestrator.domain.model.vo.SignalName;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
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
public class VirtualThreadStrategy implements ExecutionStrategy {

    private final DataFactory dataFactory;

    @Override
    public String getType() { return "ASYNC"; }

    @Override
    public Map<String, Object> execute(Pipeline pipeline, Map<String, Object> rawInput) {
        Map<Signal, DataNode> dataInputs = new HashMap<>();
        if (rawInput != null) {
            rawInput.forEach((k, v) -> dataInputs.put(SignalName.of(k), dataFactory.createValue(v)));
        }
        dataInputs.put(CoreSignals.RAW, dataFactory.createValue(rawInput));
        
        return executeWithDataNodes(pipeline, dataInputs);
    }

    private Map<String, Object> executeWithDataNodes(Pipeline pipeline, Map<? extends Signal, DataNode> inputs) {
        var identity = OrquestratorContext.get();
        Instant deadline = ExecutionClock.calculateDeadline(pipeline.timeout());
        SignalRegistry signals = new SignalRegistry();

        signals.emitAll(inputs);

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            pipeline.getNodes().forEach(node -> scope.fork(() -> {
                node.run(signals);
                return null;
            }));

            try {
                scope.joinUntil(deadline);
                scope.throwIfFailed();
            } catch (TimeoutException e) {
                log.error("!!! PIPELINE TIMEOUT [{}]: {}ms excedidos.", 
                        identity.getCorrelationId(), pipeline.timeout().toMillis());
                throw new PipelineException("Timeout atingido durante a execução do pipeline");
            } catch (Exception e) {
                log.error("!!! PIPELINE FAILURE [{}]: Falha crítica. Causa: {}", 
                        identity.getCorrelationId(), e.getMessage());
                throw e;
            }

            return collectFinalResults(pipeline, signals);

        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private Map<String, Object> collectFinalResults(Pipeline pipeline, SignalRegistry signals) {
        Map<String, Object> results = new HashMap<>();
        if (pipeline.requiredOutputs() != null) {
            pipeline.requiredOutputs().forEach(signalNameStr -> {
                SignalName signalName = SignalName.of(signalNameStr);
                SignalResult result = signals.await(signalName);
                if (result instanceof SignalResult.Present p) {
                    results.put(signalNameStr, p.value().asNative());
                }
            });
        }
        return results;
    }

    private RuntimeException handleException(Exception e) {
        if (e instanceof PipelineException p) return p;
        return new PipelineException("Erro interno na execução do pipeline", e);
    }
}
