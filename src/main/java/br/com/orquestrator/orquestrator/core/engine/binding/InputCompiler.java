package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InputCompiler {

    private final ExpressionEngine expressionEngine;

    public Function<ReadableContext, Map<String, Object>> bake(TaskDefinition def) {
        Map<String, String> inputMap = def.inputs() != null ? Map.copyOf(def.inputs()) : Map.of();
        return (reader) -> {
            if (inputMap.isEmpty()) return Map.of();
            Map<String, Object> inputs = new HashMap<>((int)(inputMap.size() / 0.75f) + 1);
            inputMap.forEach((local, global) -> inputs.put(local, reader.getRaw(global)));
            return inputs;
        };
    }

    public Set<String> extractRequiredFields(TaskDefinition def) {
        if (def.outputs() == null) return Set.of();
        return def.outputs().keySet().stream()
                .map(expr -> expressionEngine.compile(expr).rootField())
                .collect(Collectors.toSet());
    }
}
