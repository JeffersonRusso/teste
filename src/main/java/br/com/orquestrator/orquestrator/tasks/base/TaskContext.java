package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import java.util.Map;
import java.util.Set;

public record TaskContext(
    Map<String, Object> inputs,
    DataValue configuration,
    String nodeId,
    Set<String> requiredFields // Campos que o pipeline deseja extrair
) {
    @SuppressWarnings("unchecked")
    public <T> T getConfig() {
        return (T) configuration.raw();
    }

    public static TaskContext of(Map<String, Object> inputs, Object config, String nodeId, Set<String> requiredFields) {
        return new TaskContext(inputs, DataValue.of(config), nodeId, requiredFields);
    }
}
