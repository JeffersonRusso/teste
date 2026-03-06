package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ContextSchema;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.core.context.WriteableContext;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataMarshaller {

    private final ExpressionEngine expressionEngine;
    private final ContractRegistry contractRegistry;

    public MarshallingPlan createPlan(TaskDefinition def) {
        Map<OutputMapper, DataPath> outputPlan = new HashMap<>();
        if (def.outputs() != null) {
            def.outputs().forEach((localExpr, globalKey) -> {
                DataPath path = DataPath.of(localExpr);
                OutputMapper mapper = path.isIdentity() 
                    ? DataValue::of 
                    : (res) -> expressionEngine.evaluate(localExpr, res);

                outputPlan.put(mapper, DataPath.of(globalKey));
            });
        }

        return new MarshallingPlan(
            def.inputs() != null ? Map.copyOf(def.inputs()) : Map.of(),
            outputPlan,
            def.nodeId().value()
        );
    }

    public Map<String, Object> resolveInputs(MarshallingPlan plan, ReadableContext reader) {
        if (plan.inputMap().isEmpty()) return Map.of();
        Map<String, Object> inputs = new HashMap<>((int)(plan.inputMap().size() / 0.75f) + 1);
        plan.inputMap().forEach((local, global) -> inputs.put(local, reader.getRaw(global)));
        return inputs;
    }

    public void mapOutputs(MarshallingPlan plan, TaskResult result, WriteableContext writer) {
        writer.put(ContextSchema.toNodeStatusPath(plan.nodeId()), DataValue.of(result.status()));

        if (result.isSuccess()) {
            DataValue body = result.body();
            writer.put(ContextSchema.toNodeResultPath(plan.nodeId()), body);

            plan.outputPlan().forEach((mapper, targetPath) -> {
                try {
                    DataValue extracted = mapper.map(body.raw());
                    if (!(extracted instanceof DataValue.Empty)) {
                        String semanticType = contractRegistry.get(targetPath.value())
                                .map(c -> c.definition().semanticType() != null ? c.definition().semanticType() : c.definition().type().name())
                                .orElse(null);

                        writer.put(targetPath, DataValue.of(extracted.raw(), semanticType));
                    }
                } catch (Exception e) {
                }
            });
        }
    }

    public List<NormalizationStep> createNormalizationPlan(Map<String, String> mapping) {
        if (mapping == null) return List.of();
        return mapping.entrySet().stream()
                .map(e -> new NormalizationStep(e.getKey(), e.getValue())) // Guarda a String da expressão
                .toList();
    }

    public void executeNormalization(List<NormalizationStep> plan, WriteableContext writer) {
        ReadableContext reader = ContextHolder.reader();
        for (var step : plan) {
            // Usa o evaluate unificado
            DataValue value = expressionEngine.evaluate(step.expression(), reader);
            if (!(value instanceof DataValue.Empty)) {
                DataPath globalPath = ContextSchema.toStandardPath(step.target());
                String semanticType = contractRegistry.get(globalPath.value())
                        .map(c -> c.definition().semanticType() != null ? c.definition().semanticType() : c.definition().type().name())
                        .orElse(null);

                writer.put(globalPath, DataValue.of(value.raw(), semanticType));
            }
        }
    }

    public record NormalizationStep(String target, String expression) {}
}
