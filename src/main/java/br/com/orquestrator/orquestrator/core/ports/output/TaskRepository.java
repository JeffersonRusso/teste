package br.com.orquestrator.orquestrator.core.ports.output;

import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import java.util.List;
import java.util.Optional;

/**
 * TaskRepository: A porta de saída para carregar definições de tarefas.
 */
public interface TaskRepository {
    Optional<TaskDefinition> findByName(String name);
    
    /**
     * Retorna todas as definições de tarefa cadastradas no sistema.
     * Útil para o agendador global.
     */
    List<TaskDefinition> findAll();
}
