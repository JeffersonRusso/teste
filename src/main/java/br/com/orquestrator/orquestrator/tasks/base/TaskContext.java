package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import java.util.Map;

public record TaskContext(
    Map<String, Object> inputs,
    DataValue configuration,
    String nodeId
) {
    /**
     * Retorna a configuração já convertida para o tipo esperado.
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfig() {
        return (T) configuration.raw();
    }

    public static TaskContext of(Map<String, Object> inputs, Object config, String nodeId) {
        return new TaskContext(inputs, DataValue.of(config), nodeId);
    }
}
