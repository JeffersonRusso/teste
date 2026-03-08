package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.domain.model.DataValueNavigator;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DefaultSignalProjector: Implementação clara e robusta da lógica de projeção.
 * Usa o DataValueNavigator para garantir que caminhos e o ponto '.' funcionem sempre.
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
            // Usa o Navigator para extrair o sub-path (ex: 'id' de 'perfil_cliente.id')
            inputs.put(localKey, DataValueNavigator.navigate(signalValue, path.getSubPathObject()));
        });
        return inputs;
    }

    @Override
    public void projectOut(DataValue result, SignalRegistry signals) {
        Map<String, Map<String, Object>> groups = new HashMap<>();

        mappings.forEach((localKey, path) -> {
            // Usa o Navigator para extrair o campo local (suporta '.')
            DataValue val = DataValueNavigator.navigate(result, localKey);

            if (path.isSignalOnly()) {
                signals.emit(path.value(), val);
            } else {
                groups.computeIfAbsent(path.getRoot(), k -> new HashMap<>())
                      .put(path.getLeafName(), val.raw());
            }
        });

        groups.forEach((signalName, fields) -> {
            signals.emit(signalName, DataValueFactory.of(fields));
        });
    }

    @Override
    public void fail(SignalRegistry signals, Throwable cause) {
        mappings.values().stream()
                .map(DataPath::getRoot)
                .distinct()
                .forEach(signalName -> signals.fail(signalName, cause));
    }
}
