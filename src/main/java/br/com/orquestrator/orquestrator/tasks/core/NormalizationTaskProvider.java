package br.com.orquestrator.orquestrator.tasks.core;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * NormalizationTaskProvider: Fábrica para o nó de normalização.
 */
@Component
@RequiredArgsConstructor
public class NormalizationTaskProvider implements TaskProvider {

    @Override public String getType() { return "NORMALIZATION"; }

    @Override public Optional<Class<?>> getConfigClass() { return Optional.empty(); }

    @Override
    @SuppressWarnings("unchecked")
    public Task create(TaskDefinition definition) {
        // Extrai as regras de mapeamento da configuração
        Map<String, String> rules = (Map<String, String>) definition.config().get("rules");
        return new NormalizationTask(rules);
    }
}
