package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import java.util.Optional;

public interface TaskProvider {
    String getType();
    Task create(TaskDefinition definition);

    /**
     * Retorna a classe de configuração técnica da task, se houver.
     * Usado pelo compilador para automatizar a resolução de templates.
     */
    default Optional<Class<?>> getConfigClass() {
        return Optional.empty();
    }
}
