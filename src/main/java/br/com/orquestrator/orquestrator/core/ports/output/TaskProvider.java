package br.com.orquestrator.orquestrator.core.ports.output;

import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.api.task.Task;
import java.util.Optional;

/**
 * TaskProvider: Contrato para criação de tarefas core.
 * O tipo da tarefa é definido pelo nome do Bean do Spring (@Component("TIPO")).
 */
public interface TaskProvider {
    Optional<Class<?>> getConfigClass();
    Task create(TaskDefinition definition);
}
