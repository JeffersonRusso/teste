package br.com.orquestrator.orquestrator.core.engine.strategy;

import br.com.orquestrator.orquestrator.api.signal.CoreSignals;
import br.com.orquestrator.orquestrator.api.signal.Signal;
import br.com.orquestrator.orquestrator.core.engine.runtime.SignalRegistry;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
 import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import br.com.orquestrator.orquestrator.domain.model.vo.SignalName;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SynchronousStrategy implements ExecutionStrategy {

    private final DataFactory dataFactory;

    @Override
    public String getType() { return "SYNC"; }

    @Override
    public Map<String, Object> execute(Pipeline pipeline, Map<String, Object> rawInput) {
        Map<Signal, DataNode> dataInputs = new HashMap<>();
        if (rawInput != null) {
            rawInput.forEach((k, v) -> dataInputs.put(SignalName.of(k), dataFactory.createValue(v)));
        }
        dataInputs.put(CoreSignals.RAW, dataFactory.createValue(rawInput));
        
        SignalRegistry signals = new SignalRegistry();
        signals.emitAll(dataInputs);

        pipeline.getNodes().forEach(node -> node.run(signals));

        return collectFinalResults(pipeline, signals);
    }

    private Map<String, Object> collectFinalResults(Pipeline pipeline, SignalRegistry signals) {
        Map<String, Object> results = new HashMap<>();
        if (pipeline.requiredOutputs() != null) {
            pipeline.requiredOutputs().forEach(key -> {
                // CORREÇÃO DEMÉTER: Usa o atalho do SignalResult
                signals.await(SignalName.of(key))
                       .getValueAsNative()
                       .ifPresent(val -> results.put(key, val));
            });
        }
        return results;
    }
}
