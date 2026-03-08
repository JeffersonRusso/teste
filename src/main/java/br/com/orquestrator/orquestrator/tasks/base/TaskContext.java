package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import java.util.Map;
import java.util.Set;

/**
 * TaskContext: O envelope de dados para a execução de uma Task.
 */
public record TaskContext(
    Map<String, DataValue> inputs,
    DataValue configuration,
    String nodeId,
    Set<String> requiredFields
) {
    @SuppressWarnings("unchecked")
    public <T> T getConfig() {
        return configuration != null ? (T) configuration.raw() : null;
    }

    public DataValue getInput(String key) {
        return inputs.getOrDefault(key, DataValue.EMPTY);
    }

    public TaskContext withConfiguration(DataValue newConfig) {
        return new TaskContext(inputs, newConfig, nodeId, requiredFields);
    }

    public static TaskContext of(Map<String, DataValue> inputs, Object config, String nodeId, Set<String> requiredFields) {
        return new TaskContext(inputs, DataValueFactory.of(config), nodeId, requiredFields);
    }
}
