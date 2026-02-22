package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

/**
 * Base para provedores de task que utilizam configurações tipadas (Records/POJOs).
 * Agora operando sobre Mapas Java puros para performance máxima.
 */
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
            // CORREÇÃO: convertValue é o método correto para converter Map -> Record
            C config = objectMapper.convertValue(def.getConfig(), configClass);
            if (config == null) {
                throw new TaskConfigurationException("Configuração ausente ou inválida para task " + def.getNodeId().value());
            }
            return createInternal(def, config);
        } catch (Exception e) {
            throw new TaskConfigurationException("Erro ao processar configuração da task " + def.getNodeId().value() + ": " + e.getMessage(), e);
        }
    }

    protected abstract Task createInternal(TaskDefinition def, C config);
}
