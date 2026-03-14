package br.com.orquestrator.orquestrator.tasks.interceptor.impl.semantic;

import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.semantic.SemanticRegistry;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.SemanticHandler;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * SemanticInterceptor: Governança semântica sincronizada com o domínio rico.
 */
@Slf4j
@RequiredArgsConstructor
public final class SemanticInterceptor implements TaskInterceptor {

    private final SemanticRegistry semanticRegistry;
    private final DataFactory dataFactory;

    @Override
    public TaskResult intercept(TaskExecutionContext context, TaskChain chain) {
        validateInputs(context);

        TaskResult result = chain.proceed(context);

        if (result instanceof TaskResult.Success s) {
            return applyOutputFormatting(context, s);
        }

        return result;
    }

    private void validateInputs(TaskExecutionContext context) {
        context.getDefinition().inputs().forEach(input -> {
            // CORREÇÃO: Acesso direto ao campo do record TaskInput
            if (input.expectedSemanticType() != null) {
                semanticRegistry.getHandler(input.expectedSemanticType()).ifPresent(handler -> {
                    DataNode value = context.getInput(input.localKey());
                    if (!value.isMissing() && !handler.isValid(value.asNative())) {
                        throw new RuntimeException("Falha semântica no input [" + 
                                input.localKey() + "]. Tipo esperado: " + input.expectedSemanticType());
                    }
                });
            }
        });
    }

    private TaskResult applyOutputFormatting(TaskExecutionContext context, TaskResult.Success success) {
        TaskDefinition def = context.getDefinition();
        DataNode body = success.body();
        
        if (body.isObject()) {
            Map<String, Object> formattedValues = new HashMap<>();
            boolean modified = false;

            for (var output : def.outputs()) {
                // CORREÇÃO: Acesso direto ao campo do record TaskOutput
                if (output.producedSemanticType() != null) {
                    SemanticHandler handler = semanticRegistry.getHandler(output.producedSemanticType()).orElse(null);
                    if (handler != null) {
                        DataNode val = body.get(output.localKey());
                        if (!val.isMissing()) {
                            Object formatted = handler.format(val.asNative());
                            formattedValues.put(output.localKey(), formatted);
                            modified = true;
                        }
                    }
                }
            }

            if (modified) {
                log.debug("Mapeando formatação semântica na saída de [{}]", context.getTaskName());
                Map<String, Object> finalBody = new HashMap<>((Map<String, Object>) body.asNative());
                finalBody.putAll(formattedValues);
                return TaskResult.success(dataFactory.createObject(finalBody));
            }
        }
        
        return success;
    }
}
