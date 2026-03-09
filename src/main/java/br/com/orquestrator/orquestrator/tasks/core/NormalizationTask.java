package br.com.orquestrator.orquestrator.tasks.core;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.domain.model.DataValueNavigator;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * NormalizationTask: Transforma dados brutos em sinais normalizados.
 */
@RequiredArgsConstructor
public class NormalizationTask implements Task {

    private final Map<String, String> rules;

    @Override
    public TaskResult execute(Map<String, DataValue> inputs) {
        Map<String, DataValue> result = new HashMap<>();
        
        // Envolvemos os inputs em um Mapping para permitir navegação a partir da raiz
        DataValue inputRoot = DataValueFactory.of(inputs);

        rules.forEach((target, path) -> {
            DataValue value = DataValueNavigator.navigate(inputRoot, path);
            if (!value.isEmpty()) {
                result.put(target, value);
            }
        });

        return TaskResult.success(DataValueFactory.of(result));
    }
}
