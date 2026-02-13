package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;

import java.util.Map;

/**
 * Registro central de tasks instanciadas e prontas para execução.
 * Responsável pelo ciclo de vida e cache das tasks.
 */
public interface TaskRegistry {
    
    /**
     * Obtém uma task pronta pelo ID (ou cria se necessário/configurado).
     */
    Task getTask(TaskDefinition def);
    
    /**
     * Cria uma nova instância de task (bypass cache).
     */
    Task createNewTask(TaskDefinition def);
    
    /**
     * Atualiza o registro com novas tasks (Hot Reload).
     */
    void refreshRegistry(Map<NodeId, Task> newTasks);
    
    /**
     * Limpa o registro.
     */
    void clearRegistry();
}
