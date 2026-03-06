package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import java.util.Optional;

/**
 * TaskRepository: Contrato para buscar definições de tarefas individuais.
 */
public interface TaskRepository {
    Optional<TaskDefinition> findByName(String name);
}
