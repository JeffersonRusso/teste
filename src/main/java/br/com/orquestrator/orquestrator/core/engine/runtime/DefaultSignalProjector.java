package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.vo.SignalBinding;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * DefaultSignalProjector: Implementação soberana e ultra-simples.
 * Delega 100% da navegação para o Jackson.
 */
@RequiredArgsConstructor
public class DefaultSignalProjector implements SignalProjector {

    private final Map<String, SignalBinding> inputMappings;
    private final Map<String, String> outputMappings;

    public static DefaultSignalProjector compileInputs(Map<String, SignalBinding> inputs) {
        return new DefaultSignalProjector(inputs != null ? Map.copyOf(inputs) : Map.of(), Map.of());
    }

    public static DefaultSignalProjector compileOutputs(Map<String, String> outputs) {
        return new DefaultSignalProjector(Map.of(), outputs != null ? Map.copyOf(outputs) : Map.of());
    }

    @Override
    public Map<String, JsonNode> projectIn(SignalRegistry signals) {
        if (inputMappings.isEmpty()) return Map.of();
        
        Map<String, JsonNode> inputs = new HashMap<>((int)(inputMappings.size() / 0.75) + 1);
        inputMappings.forEach((localKey, binding) -> {
            JsonNode signalValue = signals.await(binding.signalName());
            if (binding.hasPath()) {
                inputs.put(localKey, signalValue.at(binding.dataPath()));
            } else {
                inputs.put(localKey, signalValue);
            }
        });
        return inputs;
    }

    @Override
    public void projectOut(JsonNode result, SignalRegistry signals) {
        if (outputMappings.isEmpty()) return;

        outputMappings.forEach((localKey, targetSignalName) -> {
            // Se localKey for '.', usa o resultado todo. Senão, navega com / (JSON Pointer).
            JsonNode val = ".".equals(localKey) ? result : result.at("/" + localKey);
            signals.emit(targetSignalName, val);
        });
    }

    @Override
    public void fail(SignalRegistry signals, Throwable cause) {
        // Falha apenas os sinais de saída
        outputMappings.values().forEach(signalName -> signals.fail(signalName, cause));
    }
}
