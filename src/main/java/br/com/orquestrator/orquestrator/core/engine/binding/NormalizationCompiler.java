package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ContextSchema;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.core.context.WriteableContext;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NormalizationCompiler {

    private final ExpressionEngine expressionEngine;

    public List<NormalizationStep> createPlan(Map<String, String> mapping) {
        if (mapping == null) return List.of();
        return mapping.entrySet().stream()
                .map(e -> new NormalizationStep(
                    e.getKey(), 
                    // A PONTE: Transforma CompiledExpression em Function pura
                    (ctx) -> expressionEngine.compile(e.getValue()).evaluate(ctx)
                ))
                .toList();
    }

    public void execute(List<NormalizationStep> plan, WriteableContext writer) {
        ReadableContext reader = ContextHolder.reader();
        for (var step : plan) {
            // Executa a função sem saber que é SpEL por trás
            DataValue value = step.transformation().apply(reader);
            if (!(value instanceof DataValue.Empty)) {
                writer.put(ContextSchema.toStandardPath(step.target()).value(), value);
            }
        }
    }
}
