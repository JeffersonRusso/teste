/*
package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

// CLASSE DESCONTINUADA: A herança foi removida em favor da implementação direta da interface Task.
@Slf4j
public abstract class BaseTask implements Task {

    @Override
    public final TaskResult execute(Map<String, JsonNode> inputs) {
        try {
            return doExecute(inputs);
        } catch (Exception e) {
            log.error("Erro na execução da task [{}]: {}", getClass().getSimpleName(), e.getMessage());
            return TaskResult.error(500, e.getMessage());
        }
    }

    protected abstract TaskResult doExecute(Map<String, JsonNode> inputs) throws Exception;
}
*/
