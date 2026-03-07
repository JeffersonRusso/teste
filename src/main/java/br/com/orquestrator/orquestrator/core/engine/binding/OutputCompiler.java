package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.context.ContextSchema;
import br.com.orquestrator.orquestrator.core.context.WriteableContext;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.core.engine.validation.DataValidator;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import br.com.orquestrator.orquestrator.infra.el.CompiledExpression;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
public class OutputCompiler {

    private final ExpressionEngine expressionEngine;
    private final DataValidator dataValidator;
    private final ContractRegistry contractRegistry;

    public MarshallingPlan createPlan(TaskDefinition def) {
        Map<OutputMapper, DataPath> outputPlan = new HashMap<>();
        Map<String, String> rawOutputMap = def.outputs() != null ? Map.copyOf(def.outputs()) : Map.of();

        if (def.outputs() != null) {
            def.outputs().forEach((localExpr, globalKey) -> {
                // O motor agora trata o ponto "." corretamente
                CompiledExpression compiled = expressionEngine.compile(localExpr);
                outputPlan.put(compiled::evaluate, DataPath.of(globalKey));
            });
        }

        return new MarshallingPlan(
            def.inputs() != null ? Map.copyOf(def.inputs()) : Map.of(),
            outputPlan,
            rawOutputMap,
            def.nodeId().value()
        );
    }

    public BiConsumer<TaskResult, WriteableContext> bake(TaskDefinition def) {
        String nodeId = def.nodeId().value();
        Map<String, String> outputs = def.outputs() != null ? Map.copyOf(def.outputs()) : Map.of();

        return (result, writer) -> {
            writer.put(ContextSchema.toNodeStatusPath(nodeId), DataValue.of(result.status()));
            if (!result.isSuccess()) return;

            DataValue body = result.body();
            writer.put(ContextSchema.toNodeResultPath(nodeId), body);

            outputs.forEach((localExpr, globalKey) -> {
                try {
                    // Usa o motor unificado para extrair o dado
                    DataValue extracted = expressionEngine.compile(localExpr).evaluate(body.raw());

                    if (!(extracted instanceof DataValue.Empty)) {
                        contractRegistry.get(globalKey).ifPresent(c -> dataValidator.validate(c, extracted.raw()));
                        writer.put(globalKey, extracted);
                    }
                } catch (Exception e) {
                }
            });
        };
    }
}
