package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.api.signal.SignalResult;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import br.com.orquestrator.orquestrator.domain.model.vo.DataBinding;
import lombok.RequiredArgsConstructor;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * LazySignalMap: Resolve sinais e navega neles usando Smart Navigation.
 */
@RequiredArgsConstructor
public class LazySignalMap extends AbstractMap<String, DataNode> {

    private final SignalRegistry registry;
    private final Map<String, DataBinding> inputMappings;
    private final DataFactory dataFactory;

    @Override
    public DataNode get(Object key) {
        DataBinding binding = inputMappings.get(key);
        if (binding == null) return null;

        SignalResult result = registry.await(binding.signal());

        return switch (result) {
            case SignalResult.Present p -> {
                // CORREÇÃO: Usa 'find' para detecção automática de ponteiro ou chave.
                yield p.value().find(binding.pointer());
            }
            case SignalResult.Empty e -> dataFactory.missing();
            case SignalResult.Failed f -> throw new RuntimeException("Falha na dependência: " + binding.signal().signalName(), f.cause());
            case SignalResult.Pending p -> throw new RuntimeException("Sinal não resolvido: " + binding.signal().signalName());
        };
    }

    @Override
    public boolean containsKey(Object key) {
        return inputMappings.containsKey(key);
    }

    @Override
    public Set<Entry<String, DataNode>> entrySet() {
        return inputMappings.keySet().stream()
                .map(key -> new SimpleImmutableEntry<>(key, get(key)))
                .collect(Collectors.toSet());
    }
}
