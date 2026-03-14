package br.com.orquestrator.orquestrator.tasks.normalization;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * NormalizationTask: Normaliza dados usando Smart Navigation.
 */
@Slf4j
@RequiredArgsConstructor
public final class NormalizationTask implements Task {

    private final Map<String, String> rules;
    private final DataFactory dataFactory;

    @Override
    public TaskResult execute(TaskExecutionContext context) {
        try {
            DataNode raw = context.getInput("raw");
            Map<String, Object> resultValues = new HashMap<>();

            rules.forEach((targetField, sourcePath) -> {
                // CORREÇÃO: Usa 'find' para navegar no input bruto de forma inteligente.
                DataNode valueNode = raw.find(sourcePath);
                resultValues.put(targetField, valueNode.asNative());
            });

            return TaskResult.success(dataFactory.createObject(resultValues));

        } catch (Exception e) {
            log.error("Erro na normalização [{}]: {}", context.getTaskName(), e.getMessage());
            return TaskResult.error(500, "Falha na normalização: " + e.getMessage());
        }
    }
}
