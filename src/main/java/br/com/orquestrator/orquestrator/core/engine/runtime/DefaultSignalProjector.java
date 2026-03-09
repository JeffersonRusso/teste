package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueNavigator;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DefaultSignalProjector: Implementação soberana e ultra-simples.
 * SRP: Apenas transporta dados entre o Registry e as Tasks.
 * Não realiza montagem de objetos (Reshaping), apenas nomeia sinais.
 */
@RequiredArgsConstructor
public class DefaultSignalProjector implements SignalProjector {

    private final Map<String, DataPath> mappings;

    public static DefaultSignalProjector compile(Map<String, String> rawMappings) {
        Map<String, DataPath> compiled = rawMappings.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> DataPath.of(e.getValue())));
        return new DefaultSignalProjector(compiled);
    }

    @Override
    public Map<String, DataValue> projectIn(SignalRegistry signals) {
        Map<String, DataValue> inputs = new HashMap<>((int)(mappings.size() / 0.75) + 1);
        mappings.forEach((localKey, path) -> {
            DataValue signalValue = signals.await(path.getRoot());
            inputs.put(localKey, DataValueNavigator.navigate(signalValue, path.getSubPathObject()));
        });
        return inputs;
    }

    @Override
    public void projectOut(DataValue result, SignalRegistry signals) {
        // ALOCAÇÃO ZERO & COMPLEXIDADE ZERO:
        // Apenas emite o que a task produziu sob o nome do sinal desejado.
        mappings.forEach((localKey, path) -> {
            DataValue val = DataValueNavigator.navigate(result, localKey);
            signals.emit(path.value(), val);
        });
    }

    @Override
    public void fail(SignalRegistry signals, Throwable cause) {
        mappings.values().forEach(path -> signals.fail(path.getRoot(), cause));
    }
}
