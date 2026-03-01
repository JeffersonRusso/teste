package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TypedTaskProvider<C> implements TaskProvider {

    protected final ObjectMapper objectMapper;
    private final Class<C> configClass;
    private final String type;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Task create(TaskDefinition def) {
        try {
            C config = objectMapper.convertValue(def.config(), configClass);
            return createInternal(def, config);
        } catch (Exception e) {
            throw new TaskConfigurationException("Erro na config da task " + def.nodeId().value(), e);
        }
    }

    protected abstract Task createInternal(TaskDefinition def, C config);
}
