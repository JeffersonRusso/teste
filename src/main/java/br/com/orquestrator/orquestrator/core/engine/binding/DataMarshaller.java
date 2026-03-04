package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ContextSchema;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.core.context.WriteableContext;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataMarshaller {

    private final ExpressionEngine expressionEngine;

    public MarshallingPlan createPlan(TaskDefinition def) {
        return new MarshallingPlan(
            def.inputs() != null ? Map.copyOf(def.inputs()) : Map.of(),
            def.outputs() != null ? Map.copyOf(def.outputs()) : Map.of(),
            def.nodeId().value()
        );
    }

    public List<NormalizationStep> createNormalizationPlan(Map<String, String> mapping) {
        if (mapping == null) return List.of();
        return mapping.entrySet().stream()
                .map(e -> new NormalizationStep(e.getKey(), e.getValue()))
                .toList();
    }

    public void executeNormalization(List<NormalizationStep> plan, WriteableContext writer) {
        for (var step : plan) {
            Object value = expressionEngine.evaluate(step.expression(), ContextHolder.reader(), Object.class);
            if (value != null) {
                writer.put(ContextSchema.toStandardPath(step.target()), value);
            }
        }
    }

    public Map<String, Object> resolveInputs(MarshallingPlan plan, ReadableContext reader) {
        if (plan.inputMap().isEmpty()) return Map.of();
        Map<String, Object> inputs = new HashMap<>((int)(plan.inputMap().size() / 0.75f) + 1);
        plan.inputMap().forEach((local, global) -> inputs.put(local, reader.get(global)));
        return inputs;
    }

    public void mapOutputs(MarshallingPlan plan, TaskResult result, WriteableContext writer) {
        // Usa o SCHEMA para status
        writer.put(ContextSchema.toNodeStatusPath(plan.nodeId()), result.status());

        if (result.isSuccess() && result.body() != null) {
            Object rawBody = result.body().raw();
            
            // Usa o SCHEMA para o resultado bruto do nó
            writer.put(ContextSchema.toNodeResultPath(plan.nodeId()), rawBody);

            plan.outputMap().forEach((local, global) -> writer.put(global, rawBody));
        }
    }

    public record NormalizationStep(String target, String expression) {}
}
