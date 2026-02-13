package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.DataValue;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Path;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação de TaskData que enforça o contrato e suporta navegação em caminhos.
 * Atua como um adaptador, expondo uma visão de Map para engines externas via asMap().
 */
public class ContractView extends AbstractMap<String, Object> implements TaskData {

    private final ExecutionContext context;
    private final TaskDefinition definition;
    private final Set<String> allowedInputs;
    private final Set<String> allowedOutputs;

    public ContractView(ExecutionContext context, TaskDefinition definition) {
        this.context = context;
        this.definition = definition;
        this.allowedInputs = computeAllowedPaths(definition.getRequires());
        this.allowedOutputs = extractNames(definition.getProduces());
    }

    @Override
    public DataValue get(String key) {
        if (!allowedInputs.contains(key)) return DataValue.of(null);
        
        Object value = context.get(key);
        if (value != null) return DataValue.of(value);

        Path path = Path.of(key);
        return path.isNested() ? DataValue.of(resolveNestedPath(path)) : DataValue.of(null);
    }

    private Object resolveNestedPath(Path path) {
        String[] segments = path.segments();
        Object current = context.get(segments[0]);

        for (int i = 1; i < segments.length && current != null; i++) {
            current = extractProperty(current, segments[i]);
        }
        return current;
    }

    private Object extractProperty(Object obj, String property) {
        return switch (obj) {
            case Map<?, ?> map -> map.get(property);
            case JsonNode node -> {
                JsonNode child = node.path(property);
                yield child.isMissingNode() ? null : child;
            }
            case null, default -> null;
        };
    }

    @Override
    public Object put(String key, Object value) {
        if (!allowedOutputs.contains(key)) {
            throw new SecurityException(STR."Acesso negado: A task não declarou '\{key}' como saída.");
        }
        Object previous = context.get(key);
        context.put(key, value);
        return previous;
    }

    @Override
    public boolean has(String key) {
        return allowedInputs.contains(key) && get(key).isPresent();
    }

    @Override
    public void addMetadata(String key, Object value) {
        context.addTaskMetadata(definition.getNodeId().value(), key, value);
    }

    @Override
    public Object getMetadata(String key) {
        return TaskMetadataHelper.get(context, definition.getNodeId().value(), key);
    }

    @Override
    public Map<String, Object> asMap() {
        return new AbstractMap<String, Object>() {
            @Override
            public Set<Entry<String, Object>> entrySet() {
                return allowedInputs.stream()
                        .map(k -> {
                            Object raw = ContractView.this.get(k).unwrap();
                            return raw != null ? new AbstractMap.SimpleImmutableEntry<>(k, raw) : null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }

            @Override
            public Object get(Object key) {
                return (key instanceof String s) ? ContractView.this.get(s).unwrap() : null;
            }

            @Override
            public boolean containsKey(Object key) {
                return (key instanceof String s) && ContractView.this.has(s);
            }
        };
    }

    // --- Implementação de Map para a própria ContractView (para compatibilidade com AbstractMap) ---

    @Override
    public Object get(Object key) {
        return (key instanceof String s) ? get(s).unwrap() : null;
    }

    @Override
    public boolean containsKey(Object key) {
        return key instanceof String s && allowedInputs.contains(s) && get(s).isPresent();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return allowedInputs.stream()
                .map(k -> {
                    Object raw = get(k).unwrap();
                    return raw != null ? new AbstractMap.SimpleImmutableEntry<>(k, raw) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<String> computeAllowedPaths(List<DataSpec> specs) {
        if (specs == null || specs.isEmpty()) return Set.of();
        return specs.stream()
                .map(DataSpec::name)
                .map(Path::of)
                .flatMap(path -> path.hierarchy().stream())
                .map(Path::value)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<String> extractNames(List<DataSpec> specs) {
        if (specs == null || specs.isEmpty()) return Set.of();
        return specs.stream()
                .map(DataSpec::name)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public int size() { return allowedInputs.size(); }
}
