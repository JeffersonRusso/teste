package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import java.util.Optional;

/**
 * TaskDefinitionProvider: Abstração para buscar configurações de tarefas.
 */
public interface TaskDefinitionProvider {
    Optional<TaskDefinition> getDefinition(String name);
}
